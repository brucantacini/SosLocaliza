export type RiskLevel = 'high' | 'moderate' | 'low' | 'unavailable'

export type WeatherSample = {
  id: string
  latitude: number
  longitude: number
  region: string
  precipitationByHour: Array<number | null>
  probabilityByHour: Array<number | null>
  fetchedAt: string
  level: RiskLevel
  totalPrecipitation24h: number | null
  maximumPrecipitationHour: number | null
  maximumPrecipitationProbability: number | null
}

export const RISK_THRESHOLDS = {
  highHourlyMm: 20,
  high24hMm: 50,
  moderateHourlyMm: 5,
  moderate24hMm: 20,
  moderateProbabilityPercent: 60,
} as const

function finiteValues(values: Array<number | null>) {
  return values.filter((value): value is number => value !== null && Number.isFinite(value))
}

export function classifyWeather(
  precipitationByHour: Array<number | null>,
  probabilityByHour: Array<number | null>,
): Pick<WeatherSample, 'level' | 'totalPrecipitation24h' | 'maximumPrecipitationHour' | 'maximumPrecipitationProbability'> {
  const rain = finiteValues(precipitationByHour.slice(0, 24))
  const probabilities = finiteValues(probabilityByHour.slice(0, 24))
  const completeRain = rain.length === 24 && precipitationByHour.length >= 24
  const completeProbability = probabilities.length === 24 && probabilityByHour.length >= 24
  const totalPrecipitation24h = completeRain ? rain.reduce((sum, value) => sum + value, 0) : null
  const maximumPrecipitationHour = rain.length > 0 ? Math.max(...rain) : null
  const maximumPrecipitationProbability = probabilities.length > 0 ? Math.max(...probabilities) : null

  if (
    (maximumPrecipitationHour !== null && maximumPrecipitationHour >= RISK_THRESHOLDS.highHourlyMm) ||
    (totalPrecipitation24h !== null && totalPrecipitation24h >= RISK_THRESHOLDS.high24hMm)
  ) {
    return { level: 'high', totalPrecipitation24h, maximumPrecipitationHour, maximumPrecipitationProbability }
  }

  if (
    (maximumPrecipitationHour !== null && maximumPrecipitationHour >= RISK_THRESHOLDS.moderateHourlyMm) ||
    (totalPrecipitation24h !== null && totalPrecipitation24h >= RISK_THRESHOLDS.moderate24hMm) ||
    (maximumPrecipitationProbability !== null && maximumPrecipitationProbability >= RISK_THRESHOLDS.moderateProbabilityPercent)
  ) {
    return { level: 'moderate', totalPrecipitation24h, maximumPrecipitationHour, maximumPrecipitationProbability }
  }

  return {
    level: completeRain && completeProbability ? 'low' : 'unavailable',
    totalPrecipitation24h,
    maximumPrecipitationHour,
    maximumPrecipitationProbability,
  }
}
