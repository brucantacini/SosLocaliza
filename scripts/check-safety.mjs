import { Browser, sleep } from './lib/cdp.mjs'
import { mkdirSync, writeFileSync } from 'node:fs'
import assert from 'node:assert/strict'

const out='.impeccable/review/safety'
mkdirSync(out,{recursive:true})
const browser=await Browser.launch()
const cases=[
  ['Alagamento','Alagamento'],
  ['Enchente','Enchente'],
  ['Desmoronamento ou barranco em risco','Desmoronamento ou deslizamento de terra'],
  ['Casa com risco de desabamento','Risco de desabamento ou casa cedendo'],
  ['Outra situação','Outras situações de emergência'],
]
const results=[]
const sources=new Map()
const url=(process.env.SOS_URL || 'http://127.0.0.1:5173/')
const records=page=>page.eval(()=>localStorage.getItem('sos-localiza-alerts'))
async function click(page,text,selector='button'){
  assert.ok(await page.eval(({text,selector})=>{
    const el=[...document.querySelectorAll(selector)].find(el=>el.getClientRects().length&&el.textContent.includes(text))
    el?.click();return !!el
  },{text,selector}),`Missing control: ${text}`)
  await sleep(120)
}
async function theme(page,value){await page.eval(value=>document.querySelector(`[aria-label="Tema ${value==='dark'?'escuro':'claro'}"]`).click(),value);await sleep(60)}
async function snapshot(page,name,width,mode){
  await page.eval(()=>document.fonts.ready)
  await page.eval(()=>window.scrollTo(0,0))
  const state=await page.eval(()=>({
    theme:document.documentElement.dataset.theme,
    overflow:document.documentElement.scrollWidth>innerWidth,
    clipped:[...document.querySelectorAll('button,select,h1,h2,h3,summary')].filter(el=>{
      if(!el.getClientRects().length||el.closest('.leaflet-container'))return false
      const r=el.getBoundingClientRect();return r.left< -1||r.right>innerWidth+1
    }).map(el=>el.textContent.slice(0,70)),
    activeNav:[...document.querySelectorAll('nav button[aria-current="page"]')].filter(el=>el.getClientRects().length).length,
  }))
  assert.equal(state.theme,mode)
  assert.equal(state.overflow,false,`${name} ${width}: overflow`)
  assert.deepEqual(state.clipped,[],`${name} ${width}: clipped`)
  await page.screenshot({path:`${out}/${name}-${mode}-${width}.png`,format:'png',fullPage:true})
  results.push({name,width,...state})
}
async function selectTopic(page,value){
  await page.eval(value=>{
    const select=document.querySelector('.safety-select select')
    select.focus();select.value=value;select.dispatchEvent(new Event('change',{bubbles:true}))
  },value)
  await sleep(120)
  assert.ok(await page.eval(()=>document.activeElement.matches('.safety-select select')),'Keep native select keyboard focus')
}
try{
  for(const width of [1440,768,390,320]){
    const page=await browser.newPage({width,height:900,mobile:width<650})
    await page.send('Page.addScriptToEvaluateOnNewDocument',{source:`if(!sessionStorage.getItem('safety-test-ready')){localStorage.clear();sessionStorage.setItem('safety-test-ready','1')}const originalFetch=window.fetch.bind(window);window.fetch=(url,options)=>String(url).includes('api.open-meteo.com')?Promise.reject(new TypeError('Isolated guidance test')):originalFetch(url,options);`})
    await page.goto(url,{settleMs:200})
    for(let attempt=0;attempt<100;attempt++){
      if(await page.eval(()=>!!document.querySelector('form')))break
      await sleep(100)
    }
    assert.ok(await page.eval(()=>!!document.querySelector('form')),'Registration form must finish loading')
    assert.equal(await page.eval(()=>document.querySelectorAll('nav').length),0,'Guidance requires registration')
    await page.eval(()=>{
      const form=document.querySelector('form')
      for(const[name,value]of Object.entries({name:'Alex Silva (fictício)',cpf:'000.000.000-00',phone:'(00) 00000-0000',address:'Rua de Demonstração, 100 — São Paulo'}))form.elements.namedItem(name).value=value
      form.requestSubmit()
    });await sleep(300)
    for(const mode of ['light','dark']){
      await theme(page,mode)
      const before=await records(page)
      await click(page,'Orientações','nav button')
      await snapshot(page,'index',width,mode)
      assert.equal(await records(page),before,'Reading guidance must not add a record')
      assert.equal(await page.eval(()=>document.querySelectorAll('.safety-topic').length),5)
      for(const[type,title]of cases){
        await click(page,title,'.safety-topic')
        assert.equal(await page.eval(()=>document.querySelector('#safety-guide-title').textContent),title)
        assert.equal(await records(page),before)
        await click(page,'Ver todas as situações')
      }
      await click(page,'Voltar ao início')
      for(let index=0;index<cases.length;index++){
        const[type,title]=cases[index]
        const existing=JSON.parse((await records(page))||'[]')
        await click(page,'Comunicar estado de alerta')
        await click(page,type,'.occurrence-option')
        await click(page,'Continuar para revisão')
        await click(page,'Confirmar registro')
        assert.equal(await page.eval(()=>document.querySelector('.success-card h2').textContent),'Alerta registrado no SOS Localiza.')
        assert.equal(await page.eval(()=>document.querySelector('.success-lead').textContent),'A equipe da Defesa Civil foi notificada por este aplicativo. E sua Localização foi enviada com sucesso.')
        assert.ok(await page.eval(()=>parseFloat(getComputedStyle(document.querySelector('.success-lead')).fontSize)>=16),'Notification limitation must remain legible')
        assert.equal(await page.eval(()=>document.querySelectorAll('.success-simulation,.demo-notice,.prototype-chip').length),0)
        const saved=JSON.parse(await records(page))
        assert.equal(saved.length,existing.length+1)
        assert.equal(saved[0].type,type)
        assert.deepEqual(saved.slice(1),existing,'Previous records must be unchanged')
        assert.deepEqual(Object.keys(saved[0]).sort(),['date','id','location','notes','profile','source','type'])
        if(index===0||index===2)await snapshot(page,`confirmation-${index}`,width,mode)
        const savedJson=await records(page)
        await click(page,'Ver orientações de segurança')
        assert.equal(await page.eval(()=>document.activeElement.id),'safety-guide-title','Post-alert focus reaches article heading')
        assert.equal(await page.eval(()=>document.querySelector('#safety-guide-title').textContent),title)
        assert.equal(await page.eval(()=>document.querySelectorAll('.safety-guide').length),1)
        assert.equal(await page.eval(()=>document.querySelectorAll('.safety-phase-open').length),2)
        assert.equal(await page.eval(()=>document.querySelectorAll('.safety-phase[open]').length),0)
        assert.equal(await records(page),savedJson)
        assert.ok(await page.eval(()=>document.querySelector('.safety-guide').innerText.includes('199')&&document.querySelector('.safety-guide').innerText.includes('193')))
        for(const href of await page.eval(()=>[...document.querySelectorAll('.safety-references a')].map(el=>el.href)))sources.set(href,type)
        await snapshot(page,`guide-${index}`,width,mode)
        // Keyboard expands prevention, and changing theme retains its open state.
        await page.eval(()=>document.querySelector('.safety-phase summary').focus())
        await page.send('Input.dispatchKeyEvent',{type:'keyDown',key:' ',code:'Space',windowsVirtualKeyCode:32})
        await page.send('Input.dispatchKeyEvent',{type:'keyUp',key:' ',code:'Space',windowsVirtualKeyCode:32})
        await sleep(60)
        assert.equal(await page.eval(()=>document.querySelector('details.safety-phase').open),true)
        assert.notEqual(await page.eval(()=>getComputedStyle(document.activeElement).outlineStyle),'none')
        await theme(page,mode==='dark'?'light':'dark')
        assert.equal(await page.eval(()=>document.querySelector('details.safety-phase').open),true)
        await theme(page,mode)
        await selectTopic(page,cases[(index+1)%cases.length][0])
        assert.equal(await page.eval(()=>document.querySelector('#safety-guide-title').textContent),cases[(index+1)%cases.length][1])
        assert.equal(await records(page),savedJson,'Changing guide must not modify alert history')
        await click(page,'Voltar ao início')
      }
      const finalRecords=await records(page)
      await page.goto(url,{settleMs:300})
      assert.equal(await records(page),finalRecords,'Saved alerts survive reload')
      await click(page,'Histórico','nav button')
      assert.equal(await page.eval(()=>document.querySelectorAll('.history-item').length),JSON.parse(finalRecords).length)
      await click(page,'Orientações','nav button')
      if(width===390&&mode==='dark'){
        await page.send('Network.enable')
        await page.send('Network.emulateNetworkConditions',{offline:true,latency:0,downloadThroughput:0,uploadThroughput:0})
        await click(page,'Alagamento','.safety-topic')
        assert.equal(await page.eval(()=>document.querySelector('#safety-guide-title').textContent),'Alagamento')
        await selectTopic(page,'Enchente')
        assert.equal(await page.eval(()=>document.querySelector('#safety-guide-title').textContent),'Enchente')
        await page.send('Network.emulateNetworkConditions',{offline:false,latency:0,downloadThroughput:-1,uploadThroughput:-1})
      }
    }
    assert.deepEqual(page.consoleErrors,[],'No browser exceptions')
    await page.close()
    console.log(`PASS: ${width}px, both themes, five occurrence flows and read-only guidance.`)
  }
  // Follow the five actual rendered source links and confirm their new-tab targets.
  const links=await browser.newPage()
  await links.goto(url,{settleMs:300});await click(links,'Orientações','nav button')
  await click(links,'Alagamento','.safety-topic')
  for(const[href,type]of sources){
    await selectTopic(links,type)
    const point=await links.eval(href=>{
      const a=[...document.querySelectorAll('.safety-references a')].find(a=>a.href===href)
      a.scrollIntoView({block:'center'});const r=a.getBoundingClientRect();return{x:r.x+r.width/2,y:r.y+r.height/2}
    },href)
    const known=new Set((await browser.send('Target.getTargets')).targetInfos.map(t=>t.targetId))
    await links.send('Input.dispatchMouseEvent',{type:'mousePressed',...point,button:'left',clickCount:1})
    await links.send('Input.dispatchMouseEvent',{type:'mouseReleased',...point,button:'left',clickCount:1})
    await sleep(600)
    const targets=await browser.send('Target.getTargets')
    const opened=targets.targetInfos.find(t=>!known.has(t.targetId)&&t.url.startsWith(new URL(href).origin))
    assert.ok(opened,`Source link must open correct official site: ${href}`)
    await browser.send('Target.closeTarget',{targetId:opened.targetId})
  }
  await links.close()
  writeFileSync(`${out}/checks.json`,JSON.stringify({screens:results,sourceLinks:[...sources.keys()],flows:40,offlineGuidance:true,consoleErrors:[]},null,2))
  console.log(`PASS: 40 occurrence-to-guidance flows; ${results.length} captures; both themes at 1440/768/390/320; immutable records, keyboard, offline reading, 5 source links.`)
}finally{await browser.close()}
