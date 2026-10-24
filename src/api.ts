import type { Occurrence } from './occurrences'

export type Profile = { name: string; cpf: string; phone: string; address: string }
export type AlertInput = {
  requestId: string
  type: Occurrence
  location: string
  source: 'Localização do dispositivo' | 'Informada manualmente' | 'Endereço cadastrado'
  latitude: number | null
  longitude: number | null
  profile: Profile
  notes: string
}
export type ServerAlert = AlertInput & {
  id: string
  date: string
  updatedAt: string
  status: 'novo' | 'em_atendimento' | 'resolvido'
  version: number
}

const env = (import.meta as ImportMeta & { env?: { VITE_API_BASE_URL?: string } }).env
const baseUrl = env?.VITE_API_BASE_URL || 'http://localhost:3001/api/v1'

export class AlertApiError extends Error {
  constructor(message: string, readonly status: number) { super(message) }
}

export async function sendAlert(input: AlertInput, options: { fetch?: typeof fetch; timeoutMs?: number; baseUrl?: string } = {}): Promise<ServerAlert> {
  const controller = new AbortController()
  const timeout = setTimeout(() => controller.abort(), options.timeoutMs ?? 15000)
  try {
    const response = await (options.fetch ?? fetch)(`${(options.baseUrl ?? baseUrl).replace(/\/$/, '')}/alerts`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(input),
      signal: controller.signal,
    })
    const body = await response.json().catch(() => null)
    if (!response.ok) {
      const message = typeof body?.error?.message === 'string' ? body.error.message : `Falha no envio (HTTP ${response.status}).`
      const fields = body?.error?.fields
      const details = fields && typeof fields === 'object' ? Object.values(fields).filter((value) => typeof value === 'string').join(' ') : ''
      throw new AlertApiError(`${message}${details ? ` ${details}` : ''}`, response.status)
    }
    const alert = body?.data as ServerAlert | undefined
    if (![200, 201].includes(response.status) || !alert || !/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(alert.id)
      || alert.requestId !== input.requestId || !Number.isFinite(Date.parse(alert.date)) || !Number.isFinite(Date.parse(alert.updatedAt))
      || !['novo', 'em_atendimento', 'resolvido'].includes(alert.status) || !Number.isInteger(alert.version) || alert.version < 1
      || (['type', 'location', 'source', 'latitude', 'longitude', 'notes'] as const).some((key) => alert[key] !== input[key])
      || !alert.profile || (['name', 'cpf', 'phone', 'address'] as const).some((key) => alert.profile[key] !== input.profile[key])) {
      throw new Error('A API não retornou uma confirmação válida. Tente novamente com o mesmo envio.')
    }
    return alert
  } catch (error) {
    if (controller.signal.aborted) throw new Error('O envio excedeu o tempo de espera. A gravação pode ter ocorrido; tente novamente para recuperar a confirmação sem duplicar o alerta.')
    if (error instanceof TypeError) throw new Error('Não foi possível conectar à API. Verifique sua conexão e tente novamente.')
    throw error
  } finally {
    clearTimeout(timeout)
  }
}
