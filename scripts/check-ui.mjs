import { Browser, sleep } from './lib/cdp.mjs'
import { mkdirSync, writeFileSync } from 'node:fs'
import assert from 'node:assert/strict'

const out = '.impeccable/review/themes'
mkdirSync(out, { recursive: true })
const browser = await Browser.launch()
const results = []
const setup = `
window.testWeather = 'valid'; window.testGeo = 'denied';
const originalFetch = window.fetch.bind(window);
window.fetch = async (input, options) => {
  const url = String(input);
  if (!url.includes('api.open-meteo.com') || window.testWeather === 'live') return originalFetch(input, options);
  if (window.testWeather === 'error') throw new TypeError('Test network unavailable');
  if (window.testWeather === 'empty') return new Response('[]', {status:200});
  const params = new URL(url).searchParams;
  const lat = params.get('latitude').split(',').map(Number);
  const lon = params.get('longitude').split(',').map(Number);
  const data = lat.map((latitude,i)=>({latitude,longitude:lon[i],hourly:{
    precipitation:Array(24).fill(i%3===0?3:i%3===1?1:0),
    precipitation_probability:Array(24).fill(i%3===0?90:i%3===1?65:10)
  }}));
  return new Response(JSON.stringify(data), {status:200,headers:{'content-type':'application/json'}});
};
Object.defineProperty(navigator,'geolocation',{configurable:true,value:{getCurrentPosition(success,error){
 if(window.testGeo==='allowed') success({coords:{latitude:-23.61,longitude:-46.72}});
 else error({code:1,PERMISSION_DENIED:1});
}}});`
async function click(page, text, selector = 'button') {
  const found = await page.eval(({text, selector}) => {
    const buttons = [...document.querySelectorAll(selector)].filter(el => el.getClientRects().length)
    const el = buttons.find(el => el.textContent.trim().includes(text))
    if (!el) return false
    el.click(); return true
  }, { text, selector })
  assert.ok(found, `Control not found: ${text}`)
  await sleep(180)
}
async function capture(page, name, width) {
  const original = await page.eval(() => {
    window.mapBeforeTheme = document.querySelector('.interactive-map')
    return [...document.querySelectorAll('input,textarea')].map(el=>el.value)
  })
  for (const theme of ['light','dark']) {
    await page.eval(theme => document.querySelector(`[aria-label="Tema ${theme==='dark'?'escuro':'claro'}"]`).click(),theme)
    await sleep(80)
    assert.equal(await page.eval(()=>document.documentElement.dataset.theme),theme)
    assert.equal(await page.eval(()=>document.querySelector('.interactive-map')===window.mapBeforeTheme),true,'Theme must not recreate the map')
    assert.deepEqual(await page.eval(()=>[...document.querySelectorAll('input,textarea')].map(el=>el.value)),original,'Theme must preserve form values')
  await page.eval(() => document.fonts.ready)
  await page.eval(() => window.scrollTo(0,0))
  const state = await page.eval(() => ({
    title: document.querySelector('h1')?.textContent,
    overflow: document.documentElement.scrollWidth > innerWidth,
    fontLoaded: document.fonts.check('500 16px Montserrat'),
    theme: document.documentElement.dataset.theme,
    activeThemes: document.querySelectorAll('.theme-toggle [aria-pressed="true"]').length,
    clipped: [...document.querySelectorAll('button,input,textarea,h1,h2,strong')].filter(el=>{
      if (!el.getClientRects().length || el.closest('.leaflet-container')) return false
      const r=el.getBoundingClientRect(); return r.left < -1 || r.right > innerWidth+1
    }).map(el=>el.textContent.slice(0,80)),
  }))
  assert.equal(state.overflow,false, `${name} ${width}: horizontal overflow`)
  assert.deepEqual(state.clipped, [], `${name} ${width}: clipped control`)
  assert.equal(state.fontLoaded,true, 'Local font did not load')
  assert.equal(state.activeThemes,1,'Exactly one theme must be indicated as active')
  await page.screenshot({path:`${out}/${name}-${theme}-${width}.png`,format:'png',fullPage:true})
  results.push({name,width,...state,errors:[...page.consoleErrors]})
  }
}
try {
  for (const width of [1440,768,390]) {
    const page=await browser.newPage({width,height:900,mobile:width===390})
    await page.send('Page.addScriptToEvaluateOnNewDocument',{source:setup})
    await page.goto((process.env.SOS_URL || 'http://127.0.0.1:5173/'),{settleMs:300})
    await page.eval(()=>{localStorage.clear();location.reload()})
    await sleep(400)
    await capture(page,'register',width)
    await page.eval(()=>{
      const form=document.querySelector('form')
      for(const [name,value] of Object.entries({name:'Alex Silva (fictício)',cpf:'000.000.000-00',phone:'(00) 00000-0000',address:'Rua de Demonstração, 100 — São Paulo'})) form.elements.namedItem(name).value=value
      form.requestSubmit()
    })
    await sleep(2500)
    for (let attempt=0;attempt<12;attempt++) {
      if(await page.eval(()=>document.querySelectorAll('.risk-marker').length===9)) break
      await sleep(500)
    }
    await capture(page,'home-fixture',width)
    const markerCount=await page.eval(()=>document.querySelectorAll('.risk-marker').length)
    if(markerCount===9) {
      await page.eval(()=>document.querySelector('.risk-marker')?.dispatchEvent(new MouseEvent('click',{bubbles:true})))
      await sleep(200)
      assert.ok(await page.eval(()=>document.querySelector('.weather-detail-card')?.textContent.includes('Atenção alta')),'Marker selection')
    } else {
      assert.ok(await page.eval(()=>document.querySelector('.map-status-overlay')?.textContent.includes('mapa base está indisponível')),'Missing tiles must be explicitly identified')
      results.push({name:'tiles-unavailable',width,markerCount})
    }
    await page.eval(()=>document.querySelector('.leaflet-control-zoom-in').click())
    await page.eval(()=>document.querySelector('.map-recenter').click())
    await click(page,'Usar minha localização')
    assert.ok(await page.eval(()=>document.body.textContent.includes('não foi concedida')),'Denied geolocation feedback')
    await page.eval(()=>window.testGeo='allowed')
    await click(page,'Usar minha localização')
    assert.ok(await page.eval(()=>document.body.textContent.includes('Sua localização atual')),'Allowed geolocation')
    await capture(page,'location-allowed-fixture',width)
    await page.eval(()=>window.testWeather='error')
    await click(page,'Atualizar')
    assert.equal(await page.eval(()=>document.querySelectorAll('.risk-marker').length),0)
    await capture(page,'weather-error',width)
    await page.eval(()=>window.testWeather='empty')
    await click(page,'Atualizar')
    assert.ok(await page.eval(()=>document.body.textContent.includes('suficientes')),'Empty API response')
    await page.eval(()=>window.testWeather='valid')
    await click(page,'Atualizar')
    await click(page,'Histórico')
    await capture(page,'history-empty',width)
    await click(page,'Novo alerta')
    await click(page,'Desmoronamento ou barranco em risco')
    await capture(page,'choose',width)
    await page.eval(()=>window.testGeo='denied')
    await click(page,'Usar localização do dispositivo')
    assert.ok(await page.eval(()=>document.body.textContent.includes('Não foi possível acessar')),'Alert location fallback')
    await click(page,'Continuar para revisão')
    await capture(page,'review',width)
    await click(page,'Confirmar registro')
    await capture(page,'success',width)
    await click(page,'Consultar histórico')
    await capture(page,'history',width)
    await click(page,'Desmoronamento ou barranco em risco')
    await capture(page,'detail',width)
    await click(page,'Voltar ao histórico')
    await click(page,width===390?'Perfil':'Meus dados')
    await capture(page,'profile',width)
    await click(page,'Editar')
    await capture(page,'profile-edit',width)
    await click(page,'Salvar alterações')
    await page.goto((process.env.SOS_URL || 'http://127.0.0.1:5173/'),{settleMs:1000})
    assert.equal(await page.eval(()=>JSON.parse(localStorage.getItem('sos-localiza-alerts')).length),1,'Persistence after reload')
    assert.deepEqual(page.consoleErrors,[],'Browser exceptions')
    await page.close()
  }
  // Check the actual external forecast independently from deterministic UI fixtures.
  const live=await browser.newPage({width:1440,height:900})
  await live.goto((process.env.SOS_URL || 'http://127.0.0.1:5173/'),{settleMs:5000})
  await live.screenshot({path:`${out}/home-live-1440.png`,format:'png',fullPage:true})
  results.push({name:'live-network',text:await live.eval(()=>document.querySelector('.map-section')?.innerText)})
  await live.close()
  writeFileSync(`${out}/checks.json`,JSON.stringify(results,null,2))
  console.log(`PASS: ${results.filter(x=>x.theme).length} screen captures, both themes at 3 widths, preserved forms/map, complete alert flow, persistence, geolocation, API valid/error/empty. Browser fixtures only.`)
} finally { await browser.close() }
