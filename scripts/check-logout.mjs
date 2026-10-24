import { Browser, sleep } from './lib/cdp.mjs'
import assert from 'node:assert/strict'
import { mkdirSync, writeFileSync } from 'node:fs'

const url=process.env.SOS_URL || 'http://127.0.0.1:5173/'
const browser=await Browser.launch()
const out='.impeccable/review/logout'
mkdirSync(out,{recursive:true})
const results=[]
const profile={name:'Alex Silva (fictício)',cpf:'000.000.000-00',phone:'(00) 00000-0000',address:'Rua de Demonstração, 100 — São Paulo'}
const fixture={id:'SL-TESTE1',type:'Alagamento',date:'2026-10-24T12:00:00.000Z',location:profile.address,source:'Endereço cadastrado',profile,notes:''}
const setup=`const realFetch=window.fetch.bind(window);window.fetch=(input,options)=>String(input).includes('api.open-meteo.com')?Promise.reject(new TypeError('Isolated logout test')):realFetch(input,options);`
async function waitFor(page, fn, message){
  for(let i=0;i<80;i++){if(await page.eval(fn))return;await sleep(100)}
  assert.fail(message)
}
async function click(page,text,selector='button'){
  assert.ok(await page.eval(({text,selector})=>{
    const el=[...document.querySelectorAll(selector)].find(el=>el.getClientRects().length&&el.textContent.trim().includes(text))
    el?.click();return !!el
  },{text,selector}),`Missing control: ${text}`)
  await sleep(100)
}
async function newPage(width=390, signedIn=true){
  const page=await browser.newPage({width,height:900,mobile:width<650})
  await page.send('Page.addScriptToEvaluateOnNewDocument',{source:setup})
  await page.goto(url,{settleMs:200})
  await waitFor(page,()=>!!document.querySelector('.brand'),'App ready')
  await page.eval(({profile,fixture,signedIn})=>{
    localStorage.clear()
    if(signedIn)localStorage.setItem('sos-localiza-profile',JSON.stringify(profile))
    localStorage.setItem('sos-localiza-alerts',JSON.stringify([fixture]))
    localStorage.setItem('sos-localiza-theme','light')
    localStorage.setItem('unrelated-preference','preserve')
  },{profile,fixture,signedIn})
  await page.goto(url,{settleMs:200})
  await waitFor(page,signedIn?()=>!!document.querySelector('nav'):()=>!!document.querySelector('form'),'Initial session state')
  return page
}
async function openProfile(page){await page.eval(()=>document.querySelector('[aria-label="Abrir perfil"]')?.click());if(!await page.eval(()=>!!document.querySelector('.logout-button')))await click(page,'Perfil','nav button');await waitFor(page,()=>!!document.querySelector('.logout-button'),'Profile ready')}
async function assertSignedOut(page){
  await waitFor(page,()=>!!document.querySelector('form')&&!document.querySelector('nav'),'Signed out registration')
  assert.deepEqual(await page.eval(()=>[...document.querySelectorAll('form input')].map(el=>el.value)),['','','',''])
  assert.equal(await page.eval(()=>localStorage.getItem('sos-localiza-profile')),null)
  assert.equal(await page.eval(()=>!!document.querySelector('.avatar,.profile-card,.detail-card,.interactive-map')),false)
}
async function register(page,name='Beatriz Silva (fictício)'){
  await page.eval(({profile,name})=>{
    const form=document.querySelector('form')
    for(const[key,value]of Object.entries({...profile,name}))form.elements.namedItem(key).value=value
    form.requestSubmit()
  },{profile,name})
  await waitFor(page,()=>!!document.querySelector('.home-intro'),'Registration works')
}
async function capture(page,name){
  await page.eval(()=>document.fonts.ready)
  assert.equal(await page.eval(()=>document.documentElement.scrollWidth>innerWidth),false,`${name}: overflow`)
  assert.deepEqual(await page.eval(()=>[...document.querySelectorAll('button,input,h1,h2')].filter(el=>el.getClientRects().length).filter(el=>{const r=el.getBoundingClientRect();return r.left< -1||r.right>innerWidth+1}).map(el=>el.textContent)),[],`${name}: clipping`)
  await page.eval(()=>window.scrollTo(0,0))
  await page.screenshot({path:`${out}/${name}.png`,format:'png',fullPage:true})
}
try{
  for(const width of [320,390,768,1440]){
    for(const theme of ['light','dark']){
      const page=await newPage(width)
      await page.eval(theme=>document.querySelector(`[aria-label="Tema ${theme==='dark'?'escuro':'claro'}"]`).click(),theme)
      await openProfile(page)
      const previous=await page.eval(()=>Object.fromEntries(Object.entries(localStorage).filter(([key])=>key!=='sos-localiza-profile')))
      assert.ok(await page.eval(()=>document.querySelector('.logout-button').getBoundingClientRect().height>=48))
      await page.eval(()=>{history.pushState(null,'','?logout-back-test');document.querySelector('.logout-button').focus()})
      await capture(page,`profile-${theme}-${width}`)
      await page.send('Input.dispatchKeyEvent',{type:'keyDown',key:' ',code:'Space',windowsVirtualKeyCode:32})
      await page.send('Input.dispatchKeyEvent',{type:'keyUp',key:' ',code:'Space',windowsVirtualKeyCode:32})
      await assertSignedOut(page)
      assert.equal(await page.eval(()=>document.activeElement.name),'name','Focus moves to empty registration')
      assert.deepEqual(await page.eval(()=>Object.fromEntries(Object.entries(localStorage))),previous,'Only profile key removed')
      await page.eval(()=>history.back());await sleep(200)
      await assertSignedOut(page)
      await capture(page,`signed-out-${theme}-${width}`)
      await page.goto(url,{settleMs:200});await assertSignedOut(page)
      assert.equal(await page.eval(()=>document.documentElement.dataset.theme),theme)
      await register(page)
      assert.equal(await page.eval(()=>!!document.querySelector('.interactive-map')),true,'Map works after registering again')
      await click(page,'Orientações','nav button')
      assert.equal(await page.eval(()=>document.querySelectorAll('.safety-topic').length),5)
      await click(page,'Voltar ao início')
      await click(page,'Comunicar estado de alerta')
      await click(page,'Continuar para revisão')
      await click(page,'Confirmar registro')
      assert.ok(await page.eval(()=>document.querySelector('.success-lead').textContent.includes('não foi notificada')))
      await click(page,'Ver orientações de segurança')
      assert.equal(await page.eval(()=>document.querySelector('#safety-guide-title').textContent),'Alagamento')
      assert.equal(await page.eval(()=>JSON.parse(localStorage.getItem('sos-localiza-alerts')).length),2)
      assert.deepEqual(page.consoleErrors,[])
      results.push(`${width}px ${theme}: keyboard logout, empty registration, Back, reload, only profile removed, new session, map/guide/alert: PASS`)
      await page.close()
    }
    console.log(`PASS: ${width}px, both themes, logout and re-entry flow`)
  }
  const first=await newPage()
  const second=await browser.newPage({width:1440,height:900})
  await second.send('Page.addScriptToEvaluateOnNewDocument',{source:setup})
  await second.goto(url,{settleMs:200});await openProfile(second)
  await openProfile(first);await click(first,'Sair');await assertSignedOut(first);await assertSignedOut(second)
  await second.goto(url,{settleMs:200});await assertSignedOut(second)
  results.push('Cross-tab logout and reopened page: PASS')
  await first.close();await second.close()

  const blocked=await newPage()
  await openProfile(blocked)
  await blocked.eval(()=>{window.originalRemove=Storage.prototype.removeItem;Storage.prototype.removeItem=function(){throw new DOMException('Blocked','SecurityError')}})
  await click(blocked,'Sair')
  assert.ok(await blocked.eval(()=>!!document.querySelector('.profile-card')&&document.querySelector('.logout-error[role="alert"]').textContent.includes('Não foi possível sair')))
  assert.ok(await blocked.eval(()=>!!localStorage.getItem('sos-localiza-profile')))
  await capture(blocked,'storage-error-light-390')
  await blocked.eval(()=>{Storage.prototype.removeItem=window.originalRemove})
  await click(blocked,'Sair');await assertSignedOut(blocked)
  await blocked.close()
  results.push('Removal failure leaves session intact, explicit error and successful retry: PASS')

  const restored=await newPage()
  await openProfile(restored)
  await restored.eval(()=>{localStorage.removeItem('sos-localiza-profile');window.dispatchEvent(new PageTransitionEvent('pageshow',{persisted:true}))})
  await assertSignedOut(restored)
  await register(restored)
  await openProfile(restored)
  const awayUrl=new URL('?history-away',url).href
  await restored.goto(awayUrl,{settleMs:200})
  const logoutTab=await browser.newPage()
  await logoutTab.send('Page.addScriptToEvaluateOnNewDocument',{source:setup})
  await logoutTab.goto(url,{settleMs:200});await openProfile(logoutTab);await click(logoutTab,'Sair')
  await restored.eval(()=>history.back());await sleep(500)
  await assertSignedOut(restored)
  await restored.close();await logoutTab.close()
  results.push('Persisted pageshow reconciliation and real cross-document Back after another-tab logout: PASS')

  const replaced=await newPage()
  await openProfile(replaced)
  await replaced.eval(profile=>{
    localStorage.removeItem('sos-localiza-profile')
    localStorage.setItem('sos-localiza-profile',JSON.stringify({...profile,name:'Another fictitious profile'}))
    window.dispatchEvent(new PageTransitionEvent('pageshow',{persisted:true}))
  },profile)
  await waitFor(replaced,()=>!!document.querySelector('form')&&!document.querySelector('.profile-card'),'Old cached profile is not restored over a newer session')
  assert.equal(await replaced.eval(()=>document.querySelector('input[name="name"]').value),'')
  assert.equal(await replaced.eval(()=>JSON.parse(localStorage.getItem('sos-localiza-profile')).name),'Another fictitious profile','Do not erase another session')
  await replaced.close()
  results.push('Restoration rejects an old profile after another person re-registers: PASS')
  const pending=await newPage()
  await pending.eval(()=>Object.defineProperty(navigator,'geolocation',{configurable:true,value:{getCurrentPosition(success,error){window.oldGeoSuccess=success;window.oldGeoError=error}}}))
  await click(pending,'Comunicar estado de alerta');await click(pending,'Usar localização do dispositivo')
  await pending.eval(()=>document.querySelector('.brand').click());await sleep(100)
  await openProfile(pending);await click(pending,'Sair');await assertSignedOut(pending)
  await register(pending)
  await click(pending,'Comunicar estado de alerta')
  await pending.eval(()=>{window.oldGeoSuccess({coords:{latitude:-22,longitude:-45}});window.oldGeoError({code:1})})
  await sleep(100)
  assert.equal(await pending.eval(()=>!!document.querySelector('.location-found,.location-error')),false)
  assert.equal(await pending.eval(()=>document.querySelector('.manual-location input').value),'')
  assert.deepEqual(pending.consoleErrors,[])
  await pending.close()
  results.push('Late geolocation success/error from old session ignored: PASS')

  const unavailable=await newPage(390,false)
  await unavailable.eval(()=>{window.originalSet=Storage.prototype.setItem;window.originalGet=Storage.prototype.getItem;window.originalRemove=Storage.prototype.removeItem;Storage.prototype.getItem=Storage.prototype.setItem=Storage.prototype.removeItem=function(){throw new DOMException('Blocked','SecurityError')}})
  await register(unavailable);await openProfile(unavailable);await click(unavailable,'Sair')
  assert.ok(await unavailable.eval(()=>!!document.querySelector('.logout-error')&&!!document.querySelector('.profile-card')))
  await unavailable.eval(()=>{Storage.prototype.setItem=window.originalSet;Storage.prototype.getItem=window.originalGet;Storage.prototype.removeItem=window.originalRemove})
  await click(unavailable,'Sair');await assertSignedOut(unavailable)
  await unavailable.close()
  results.push('Fully unavailable storage retains existing registration fallback; logout reports failure and permits retry: PASS')
  writeFileSync(`${out}/checks.json`,JSON.stringify(results,null,2))
  console.log(`PASS: ${results.length} scenarios, 17 captures, four widths/two themes, logout, storage failure, cross-tab, Back/restore and pending geolocation`)
}finally{await browser.close()}
