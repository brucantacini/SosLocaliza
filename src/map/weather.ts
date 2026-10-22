import { classifyWeather, type WeatherSample } from './risk'

export type Coordinates = { latitude: number; longitude: number }

type GridPoint = Coordinates & { id: string; region: string }

const GRID_SPACING_KM = 10

function makeGrid(center: Coordinates): GridPoint[] {
  const latitudeOffset = GRID_SPACING_KM / 111.32
  const longitudeOffset = GRID_SPACING_KM / (111.32 * Math.max(Math.cos(center.latitude * Math.PI / 180), 0.2))
  const directions = [
    ['Noroeste', -1, -1], ['Norte', -1, 0], ['Nordeste', -1, 1],
    ['Oeste', 0, -1], ['Centro', 0, 0], ['Leste', 0, 1],
    ['Sudoeste', 1, -1], ['Sul', 1, 0], ['Sudeste', 1, 1],
  ] as const

  return directions.map(([region, row, column], index) => ({
    id: `sample-${index}`,
    region: region === 'Centro' ? 'Entorno da sua localização' : `Amostra consultada ao ${region.toLowerCase()}`,
    latitude: center.latitude + row * latitudeOffset,
    longitude: center.longitude + column * longitudeOffset,
  }))
}

type ApiLocation = {
  latitude?: number
  longitude?: number
  hourly?: {
    time?: unknown[]
    precipitation?: unknown[]
    precipitation_probability?: unknown[]
  }
}

function numericArray(values: unknown[] | undefined): Array<number | null> {
  return (values || []).map((value) => typeof value === 'number' && Number.isFinite(value) ? value : null)
}

export async function fetchWeatherGrid(center: Coordinates, signal?: AbortSignal): Promise<WeatherSample[]> {
  const points = makeGrid(center)
  const params = new URLSearchParams({
    latitude: points.map((point) => point.latitude.toFixed(5)).join(','),
    longitude: points.map((point) => point.longitude.toFixed(5)).join(','),
    hourly: 'precipitation,precipitation_probability',
    forecast_hours: '24',
    timezone: 'auto',
  })

  const response = await fetch(`https://api.open-meteo.com/v1/forecast?${params.toString()}`, { signal })
  if (!response.ok) throw new Error(`A previsão respondeu com erro (${response.status}).`)

  const payload: unknown = await response.json()
  const locations = Array.isArray(payload) ? payload as ApiLocation[] : [payload as ApiLocation]
  const fetchedAt = new Date().toISOString()

  return points.map((point, index) => {
    const location = locations[index]
    const precipitationByHour = numericArray(location?.hourly?.precipitation)
    const probabilityByHour = numericArray(location?.hourly?.precipitation_probability)
    const classification = classifyWeather(precipitationByHour, probabilityByHour)

    return {
      ...point,
      latitude: typeof location?.latitude === 'number' ? location.latitude : point.latitude,
      longitude: typeof location?.longitude === 'number' ? location.longitude : point.longitude,
      precipitationByHour,
      probabilityByHour,
      fetchedAt,
      ...classification,
    }
  })
}
