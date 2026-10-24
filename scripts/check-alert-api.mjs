// Browser integration using explicitly isolated API/geolocation fixtures.
// This is NOT evidence of Oracle persistence. Requires Vite and Chrome/Edge.
import assert from 'node:assert/strict'
import { Browser, sleep } from './lib/cdp.mjs'

const url = process.env.SOS_URL || 'http://localhost:5173/'
const browser = await Browser.launch()
const profile = { name: 'Teste fictício integração', cpf: '000.000.000-00', phone: '000000000', address: 'Endereço fictício de teste' }
const setup = `
  window.testRequests = [];
  window.testStatuses = [503, 201];
  const actualFetch = window.fetch.bind(window);
  window.fetch = async (url, options) => {
    if (options?.method === 'POST' && String(url).endsWith('/alerts')) {
      const input = JSON.parse(options.body);
      window.testRequests.push(input);
      await new Promise(resolve => setTimeout(resolve, 100));
      const status = window.testStatuses.shift() || 201;
      if (status >= 400) return new Response(JSON.stringify({error:{code:'TEST_ONLY',message:'Falha de teste identificada'}}),{status});
      return new Response(JSON.stringify({data:{...input,id:'08888888-1234-4000-8000-888888888888',date:'2026-10-24T12:00:00Z',updatedAt:'2026-10-24T12:00:00Z',status:'novo',version:1}}),{status});
    }
    if (String(url).includes('api.open-meteo.com')) throw new TypeError('Isolated test');
    return actualFetch(url, options);
  };
`
async function wait(page, check, message) {
  for (let i = 0; i < 80; i++) { if (await page.eval(check)) return; await sleep(100) }
  assert.fail(message)
}
async function click(page, text) {
  assert.ok(await page.eval(text => {
    const element = [...document.querySelectorAll('button')].find(el => el.getClientRects().length && el.textContent.includes(text))
    element?.click(); return !!element
  }, text), `Missing ${text}`)
  await sleep(100)
}
async function pageFor(width) {
  const page = await browser.newPage({ width, height: 900, mobile: width < 600 })
  await page.send('Page.addScriptToEvaluateOnNewDocument', { source: setup })
  await page.goto(url, { settleMs: 200 })
  await page.eval(profile => {
    localStorage.clear()
    localStorage.setItem('sos-localiza-profile', JSON.stringify(profile))
    localStorage.setItem('sos-localiza-alerts', JSON.stringify([{ id: 'SL-LEGACY', type: 'Alagamento', date: '2026-10-24T11:00:00Z', location: profile.address, source: 'Endereço cadastrado', profile, notes: '' }]))
  }, profile)
  await page.goto(url, { settleMs: 200 })
  await wait(page, () => !!document.querySelector('.home-intro'), 'App loaded')
  return page
}
try {
  for (const width of [390, 1440]) {
    const page = await pageFor(width)
    assert.ok(await page.eval(() => document.querySelector('.home-recent-tag').textContent.includes('Legado local')))
    await click(page, 'Comunicar estado de alerta')
    await click(page, 'Continuar para revisão')
    // Two synchronous clicks must still issue exactly one request.
    await page.eval(() => {
      const button = [...document.querySelectorAll('button')].find(el => el.textContent.includes('Confirmar registro'))
      button.click(); button.click()
    })
    await wait(page, () => !!document.querySelector('.form-error[role="alert"]'), 'HTTP error shown')
    assert.equal(await page.eval(() => !!document.querySelector('.success-card')), false)
    assert.equal(await page.eval(() => JSON.parse(localStorage.getItem('sos-localiza-alerts')).length), 1)
    assert.equal(await page.eval(() => window.testRequests.length), 1)
    await click(page, 'Tentar novamente')
    await wait(page, () => !!document.querySelector('.success-card'), 'API confirmation shown')
    const requests = await page.eval(() => window.testRequests)
    assert.deepEqual(requests[0], requests[1], 'Retry keeps exact payload and requestId')
    assert.equal(requests[0].latitude, null)
    assert.equal(requests[0].longitude, null)
    assert.equal(requests[0].source, 'Endereço cadastrado')
    assert.ok(await page.eval(() => document.querySelector('.success-id').textContent.includes('08888888-1234-4000-8000-888888888888')))
    assert.equal(await page.eval(() => document.documentElement.scrollWidth > innerWidth), false, 'No horizontal overflow')
    await click(page, 'Consultar histórico')
    assert.equal(await page.eval(() => document.querySelectorAll('.history-item').length), 2)
    assert.ok(await page.eval(() => document.querySelector('.record-tag').textContent.includes('Confirmado pela API')))
    await page.eval(() => document.querySelector('[aria-label="Abrir perfil"]').click())
    await click(page, 'Sair')
    await wait(page, () => !!document.querySelector('input[name="name"]'), 'Logout returns to registration')
    assert.equal(await page.eval(() => localStorage.getItem('sos-localiza-profile')), null)
    assert.equal(await page.eval(() => JSON.parse(localStorage.getItem('sos-localiza-alerts')).length), 2)
    assert.deepEqual(page.consoleErrors, [])
    await page.close()
    console.log(`PASS ${width}px: error visibility, double-click, exact retry, API UUID, legacy/confirmed, responsive and logout`)
  }
  const gps = await pageFor(390)
  await gps.eval(() => {
    window.testStatuses = [201]
    Object.defineProperty(navigator, 'geolocation', { configurable: true, value: { getCurrentPosition(success) { success({ coords: { latitude: -23.12345678, longitude: -46.12345678 } }) } } })
  })
  await click(gps, 'Comunicar estado de alerta')
  await click(gps, 'Usar localização do dispositivo')
  await click(gps, 'Continuar para revisão')
  await click(gps, 'Confirmar registro')
  await wait(gps, () => !!document.querySelector('.success-card'), 'GPS test confirmed')
  assert.deepEqual(await gps.eval(() => [window.testRequests[0].latitude, window.testRequests[0].longitude]), [-23.12345678, -46.12345678])
  await gps.close()
  console.log('PASS: device coordinates preserve precision (explicit test fixture). Oracle NOT tested.')
} finally { await browser.close() }
