// Isolated transport tests; responses below are fixtures, never Oracle evidence.
import assert from 'node:assert/strict'
import { readFileSync } from 'node:fs'
import ts from 'typescript'
const source = ts.transpileModule(readFileSync(new URL('../src/api.ts', import.meta.url), 'utf8'), {
  compilerOptions: { target: ts.ScriptTarget.ES2022, module: ts.ModuleKind.ESNext },
}).outputText
const { sendAlert } = await import(`data:text/javascript;base64,${Buffer.from(source).toString('base64')}`)
const input = {
  requestId: '720ad1c4-bf50-4532-8db2-fb7f4314053e', type: 'Alagamento', location: 'Teste fictício',
  source: 'Informada manualmente', latitude: null, longitude: null,
  profile: { name: 'Teste fictício', cpf: '000', phone: '000', address: 'Teste fictício' }, notes: '',
}
const alert = { ...input, id: '720ad1c4-bf50-4532-8db2-fb7f4314053f', date: '2026-10-24T12:00:00Z', updatedAt: '2026-10-24T12:00:00Z', status: 'novo', version: 1 }
const json = (body, status = 200) => new Response(JSON.stringify(body), { status, headers: { 'Content-Type': 'application/json' } })
for (const status of [200, 201]) {
  const result = await sendAlert(input, { baseUrl: 'https://test.invalid/api/v1/', fetch: async (url, options) => {
    assert.equal(url, 'https://test.invalid/api/v1/alerts')
    assert.equal(options.method, 'POST')
    assert.equal(options.headers.Authorization, undefined)
    assert.deepEqual(JSON.parse(options.body), input)
    return json({ data: alert }, status)
  } })
  assert.deepEqual(result, alert)
}
for (const status of [400, 409, 503]) {
  await assert.rejects(sendAlert(input, { fetch: async () => json({ error: { code: 'TEST_ONLY', message: 'Erro visível', fields: { location: 'Corrija o local' } } }, status) }), /Erro visível.*Corrija o local/)
}
await assert.rejects(sendAlert(input, { fetch: async () => new Response('upstream unavailable', { status: 503 }) }), /HTTP 503/)
await assert.rejects(sendAlert(input, { fetch: async () => { throw new TypeError('network') } }), /conectar à API/)
for (const data of [{}, { ...alert, requestId: 'different' }, { ...alert, location: 'different' }, { ...alert, status: 'unknown' }]) {
  await assert.rejects(sendAlert(input, { fetch: async () => json({ data }) }), /confirmação válida/)
}
await assert.rejects(sendAlert(input, { timeoutMs: 5, fetch: async (_, { signal }) => new Promise((_, reject) => {
  signal.addEventListener('abort', () => reject(new DOMException('timeout', 'AbortError')))
}) }), /tempo de espera.*sem duplicar/)
console.log('PASS: transport contract, 201/200, request body, no secret, error/fields/network, invalid success and timeout. Fixtures only; Oracle not tested.')
