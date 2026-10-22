import { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'
import './MapSection.css'
import { fetchWeatherGrid, type Coordinates } from './map/weather'
import type { RiskLevel, WeatherSample } from './map/risk'

const DEMO_LOCATION: Coordinates = { latitude: -23.55052, longitude: -46.63331 }
const STALE_AFTER_MS = 30 * 60 * 1000

type LocationMode = 'device' | 'demo'
type WeatherStatus = 'idle' | 'loading' | 'ready' | 'error'

const levelText: Record<RiskLevel, string> = {
  high: 'Atenção alta',
  moderate: 'Atenção moderada',
  low: 'Atenção baixa',
  unavailable: 'Sem dados suficientes',
}

const levelColor: Record<RiskLevel, string> = {
  high: 'var(--risk-high)',
  moderate: 'var(--risk-moderate)',
  low: 'var(--risk-low)',
  unavailable: 'var(--muted)',
}

function Icon({ name, size = 18 }: { name: 'pin' | 'refresh' | 'crosshair' | 'drop' | 'check' | 'alert'; size?: number }) {
  const common = { width: size, height: size, viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', strokeWidth: 1.8, strokeLinecap: 'round' as const, strokeLinejoin: 'round' as const, 'aria-hidden': true as const }
  const paths = {
    pin: <><path d="M20 10c0 5-8 12-8 12S4 15 4 10a8 8 0 1 1 16 0Z"/><circle cx="12" cy="10" r="2.5"/></>,
    refresh: <><path d="M20 7v5h-5M4 17v-5h5"/><path d="M5.6 9A7 7 0 0 1 18 6l2 1M4 17l2-1a7 7 0 0 0 12.4-3"/></>,
    crosshair: <><circle cx="12" cy="12" r="8"/><circle cx="12" cy="12" r="3"/><path d="M12 2v2m0 16v2M2 12h2m16 0h2"/></>,
    drop: <><path d="M12 3c-3 4-6 7-6 11a6 6 0 0 0 12 0c0-4-3-7-6-11Z"/><path d="M9 15c.4 1.2 1.3 1.8 2.5 2"/></>,
    check: <path d="m5 12 4 4L19 6"/>,
    alert: <><path d="M10.3 3.9 2.5 17.4A2 2 0 0 0 4.2 20h15.6a2 2 0 0 0 1.7-2.6L13.7 3.9a2 2 0 0 0-3.4 0Z"/><path d="M12 9v4m0 4h.01"/></>,
  }
  return <svg {...common}>{paths[name]}</svg>
}

export default function MapSection() {
  const [locationMode, setLocationMode] = useState<LocationMode | null>('demo')
  const [center, setCenter] = useState<Coordinates | null>(DEMO_LOCATION)
  const [locationError, setLocationError] = useState('')
  const [weatherStatus, setWeatherStatus] = useState<WeatherStatus>('idle')
  const [weatherError, setWeatherError] = useState('')
  const [tilesUnavailable, setTilesUnavailable] = useState(false)
  const [samples, setSamples] = useState<WeatherSample[]>([])
  const [selectedId, setSelectedId] = useState('sample-4')
  const [fetchedAt, setFetchedAt] = useState<number | null>(null)
  const [clock, setClock] = useState(Date.now())
  const abortRef = useRef<AbortController | null>(null)

  const isStale = fetchedAt !== null && clock - fetchedAt > STALE_AFTER_MS
  const selectedSample = useMemo(() => samples.find((sample) => sample.id === selectedId) || samples[4] || null, [samples, selectedId])
  const handleTilesError = useCallback(() => setTilesUnavailable(true), [])
  const handleTilesLoad = useCallback(() => setTilesUnavailable(false), [])

  useEffect(() => {
    const timer = window.setInterval(() => setClock(Date.now()), 60_000)
    return () => window.clearInterval(timer)
  }, [])

  const loadForecast = useCallback(async (coordinates: Coordinates) => {
    abortRef.current?.abort()
    const controller = new AbortController()
    abortRef.current = controller
    setWeatherStatus('loading')
    setWeatherError('')
    setSamples([])
    setFetchedAt(null)

    try {
      const result = await fetchWeatherGrid(coordinates, controller.signal)
      if (result.length !== 9 || result.every((sample) => sample.level === 'unavailable')) {
        throw new Error('A API não retornou dados meteorológicos suficientes para esta região.')
      }
      setSamples(result)
      setSelectedId('sample-4')
      setFetchedAt(Date.now())
      setClock(Date.now())
      setWeatherStatus('ready')
    } catch (error) {
      if (controller.signal.aborted) return
      setWeatherStatus('error')
      setWeatherError(error instanceof TypeError ? 'Não foi possível conectar à API meteorológica agora.' : error instanceof Error ? error.message : 'Não foi possível consultar a previsão agora.')
    }
  }, [])

  useEffect(() => {
    void loadForecast(DEMO_LOCATION)
  }, [loadForecast])

  useEffect(() => () => abortRef.current?.abort(), [])

  function useDeviceLocation() {
    setLocationError('')
    if (!('geolocation' in navigator)) {
      setLocationError('Este navegador não disponibiliza localização. Você pode usar o ponto de demonstração.')
      return
    }

    navigator.geolocation.getCurrentPosition(
      ({ coords }) => {
        const point = { latitude: coords.latitude, longitude: coords.longitude }
        setCenter(point)
        setLocationMode('device')
        void loadForecast(point)
      },
      (error) => {
        const message = error.code === error.PERMISSION_DENIED
          ? 'A permissão de localização não foi concedida.'
          : 'Não foi possível obter a localização neste momento.'
        setLocationError(`${message} O mapa continua disponível no ponto de demonstração.`)
      },
      { enableHighAccuracy: false, timeout: 10_000, maximumAge: 60_000 },
    )
  }

  function useDemoLocation() {
    setCenter(DEMO_LOCATION)
    setLocationMode('demo')
    setLocationError('')
    void loadForecast(DEMO_LOCATION)
  }

  function refreshForecast() {
    if (!center) return
    void loadForecast(center)
  }

  function recenterMap() {
    window.dispatchEvent(new CustomEvent('sos-map-recenter'))
  }

  const lastQueryText = fetchedAt === null ? '' : new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short', timeStyle: 'short' }).format(fetchedAt)

  return <section className="map-section" aria-labelledby="map-title">
    <div className="map-section-heading">
      <div><h2 id="map-title">Previsão por perto</h2><p>Consulte amostras meteorológicas aproximadas ao redor da localização.</p></div>
      {center && <button className="map-refresh" onClick={refreshForecast} disabled={weatherStatus === 'loading'}><Icon name="refresh" size={16}/>{weatherStatus === 'loading' ? 'Atualizando…' : 'Atualizar'}</button>}
    </div>

    {!center ? <div className="map-consent-panel">
      <div className="map-consent-icon"><Icon name="pin" size={24}/></div>
      <div className="map-consent-copy"><strong>Veja as condições meteorológicas no seu entorno</strong><p>Com sua autorização, usamos a localização aproximada para consultar a previsão em nove pontos próximos. As coordenadas vão para a Open-Meteo; os pedidos de tiles do OpenStreetMap podem indicar a região visualizada. Nome, CPF, telefone e endereço do cadastro não são enviados.</p></div>
      {locationError && <p className="map-location-error" role="status">{locationError}</p>}
      <div className="map-consent-actions"><button className="primary-button" onClick={useDeviceLocation}><Icon name="pin" size={17}/> Usar minha localização</button><button className="secondary-button" onClick={useDemoLocation}>Ver ponto de demonstração</button></div>
      <small>Você pode negar a permissão. O restante do SOS LOCALIZA continuará funcionando normalmente.</small>
    </div> : <>
      <div className="map-context-bar"><span className={`map-location-pill ${locationMode === 'demo' ? 'demo' : ''}`}><i/>{locationMode === 'demo' ? 'Demonstração · São Paulo, SP' : 'Sua localização atual'}</span><span className="map-refresh-time">{weatherStatus === 'ready' && lastQueryText ? `Consulta feita às ${lastQueryText}` : weatherStatus === 'loading' ? 'Consultando Open-Meteo…' : weatherStatus === 'error' ? 'Previsão indisponível' : ''}</span></div>
      <div className="map-frame">
        <WeatherMap center={center} locationMode={locationMode || 'demo'} samples={samples} stale={isStale || tilesUnavailable} selectedId={selectedSample?.id || ''} onSelect={setSelectedId} onTilesError={handleTilesError} onTilesLoad={handleTilesLoad}/>
        {(weatherStatus === 'loading' || weatherStatus === 'error' || isStale || tilesUnavailable) && <div className="map-status-overlay" role="status">{tilesUnavailable ? 'O mapa base está indisponível no momento. Verifique sua conexão e tente novamente.' : weatherStatus === 'loading' ? 'Buscando dados meteorológicos…' : isStale ? 'Esta consulta tem mais de 30 minutos. Atualize para ver dados recentes.' : 'Os níveis de atenção não estão disponíveis agora.'}</div>}
        <button className="map-recenter" onClick={recenterMap} aria-label="Centralizar mapa na localização"><Icon name="crosshair" size={19}/></button>
      </div>
        {weatherStatus === 'ready' && !isStale && !tilesUnavailable && <div className="map-legend" aria-label="Legenda dos níveis de atenção"><strong>Legenda</strong><LegendRow color="#2F80ED" label={locationMode === 'demo' ? 'Demonstração' : 'Você'}/><LegendRow color={levelColor.high} label="Risco alto"/><LegendRow color={levelColor.moderate} label="Atenção moderada"/><LegendRow color={levelColor.low} label="Baixo risco estimado"/></div>}
      {locationMode === 'demo' && <div className="map-demo-change"><span>Visualizando o ponto de demonstração de São Paulo.</span><button className="map-link-button" onClick={useDeviceLocation}>Usar minha localização</button></div>}
      {locationError && <p className="map-location-error" role="status">{locationError}</p>}
      <div className="map-attribution-line"><a href="https://open-meteo.com/" target="_blank" rel="noreferrer">Dados meteorológicos: Open-Meteo</a><a href="https://open-meteo.com/en/licence" target="_blank" rel="noreferrer">CC BY 4.0</a><span>·</span><span>Mapa © OpenStreetMap contributors</span></div>
      {weatherStatus === 'error' && <div className="map-error" role="alert"><Icon name="alert" size={17}/><span>{weatherError} Tente atualizar mais tarde. Não exibimos níveis estimados sem dados válidos.</span></div>}
      {isStale && <div className="map-error stale" role="status"><Icon name="alert" size={17}/><span>Os níveis foram ocultados porque esta consulta está desatualizada. Toque em Atualizar para consultar novamente.</span></div>}
      {weatherStatus === 'ready' && !isStale && !tilesUnavailable && selectedSample && <div className="weather-detail-card">
        <div className="weather-detail-top"><span className="risk-dot" style={{ background: levelColor[selectedSample.level] }}/><div><small>ESTIMATIVA DE ATENÇÃO METEOROLÓGICA</small><strong>{levelText[selectedSample.level]}</strong></div><span className="sample-region">{selectedSample.region}</span></div>
        <div className="weather-factors"><WeatherFactor label="Maior chuva em 1h" value={formatMm(selectedSample.maximumPrecipitationHour)}/><WeatherFactor label="Precipitação em 24h" value={formatMm(selectedSample.totalPrecipitation24h)}/><WeatherFactor label="Probabilidade de chuva" value={formatPercent(selectedSample.maximumPrecipitationProbability)}/></div>
        <p className="weather-detail-time">Amostra consultada às {lastQueryText}. Os pontos indicam amostras de previsão, não limites de áreas de risco.</p>
      </div>}
    </>}

    <div className="map-disclaimer"><Icon name="alert" size={17}/><p><strong>Estimativa meteorológica, não previsão de desastre.</strong> Os dados de chuva não determinam risco local de enchente ou deslizamento e não substituem os avisos oficiais da Defesa Civil. <a href="https://avisos.inmet.gov.br/" target="_blank" rel="noreferrer">Consultar avisos meteorológicos do INMET</a>.</p></div>
    <div className="map-method"><Icon name="check" size={15}/><p>Níveis heurísticos configuráveis de precipitação e probabilidade. Chuva horária: moderado a partir de 5 mm e alto a partir de 20 mm; acumulado em 24h: moderado a partir de 20 mm e alto a partir de 50 mm; probabilidade a partir de 60% indica atenção moderada. Baixo só aparece com os dados completos abaixo desses limites.</p></div>
  </section>
}

function WeatherMap({ center, locationMode, samples, stale, selectedId, onSelect, onTilesError, onTilesLoad }: {
  center: Coordinates
  locationMode: LocationMode
  samples: WeatherSample[]
  stale: boolean
  selectedId: string
  onSelect: (id: string) => void
  onTilesError: () => void
  onTilesLoad: () => void
}) {
  const containerRef = useRef<HTMLDivElement>(null)
  const mapRef = useRef<L.Map | null>(null)
  const layersRef = useRef<L.LayerGroup | null>(null)

  useEffect(() => {
    if (!containerRef.current) return
    const map = L.map(containerRef.current, { zoomControl: true, scrollWheelZoom: false, zoomAnimation: false }).setView([center.latitude, center.longitude], 11)
    const tiles = L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
    }).addTo(map)
    let hasLoadedTile = false
    const tileTimeout = window.setTimeout(() => {
      if (!hasLoadedTile) onTilesError()
    }, 8_000)
    tiles.on('tileerror', () => {
      if (!hasLoadedTile) onTilesError()
    })
    tiles.on('tileload', () => {
      hasLoadedTile = true
      window.clearTimeout(tileTimeout)
      onTilesLoad()
    })
    tiles.on('load', () => {
      window.clearTimeout(tileTimeout)
      if (!hasLoadedTile) onTilesError()
    })
    const layer = L.layerGroup().addTo(map)
    mapRef.current = map
    layersRef.current = layer
    const recenter = () => map.setView([center.latitude, center.longitude], Math.max(map.getZoom(), 11), { animate: true })
    window.addEventListener('sos-map-recenter', recenter)
    const resizeObserver = new ResizeObserver(() => map.invalidateSize())
    resizeObserver.observe(containerRef.current)

    return () => {
      window.clearTimeout(tileTimeout)
      window.removeEventListener('sos-map-recenter', recenter)
      resizeObserver.disconnect()
      map.stop()
      map.remove()
      mapRef.current = null
      layersRef.current = null
    }
  }, [center.latitude, center.longitude, onTilesError, onTilesLoad])

  useEffect(() => {
    const layer = layersRef.current
    if (!layer || !mapRef.current) return
    layer.clearLayers()
    if (!stale) {
      samples.forEach((sample) => {
        if (sample.level === 'unavailable') return
        const riskLabel = sample.level === 'high' ? 'A' : sample.level === 'moderate' ? 'M' : 'B'
        L.circleMarker([sample.latitude, sample.longitude], {
          radius: sample.id === selectedId ? 15 : 13,
          color: '#ffffff',
          weight: sample.id === selectedId ? 4 : 3,
          fillColor: levelColor[sample.level],
          fillOpacity: 0.76,
          className: `risk-marker risk-${sample.level}`,
        }).addTo(layer).bindTooltip(riskLabel, { permanent: true, direction: 'center', className: 'risk-marker-label', opacity: 1 }).on('click', () => onSelect(sample.id))
      })
    }

    const markerHtml = locationMode === 'device'
      ? '<span class="map-person-glyph">●</span>'
      : '<span class="map-demo-glyph">D</span>'
    const userIcon = L.divIcon({ className: 'map-user-icon', html: markerHtml, iconSize: [30, 30], iconAnchor: [15, 15] })
    const userMarker = L.marker([center.latitude, center.longitude], { icon: userIcon }).addTo(layer)
    userMarker.on('click', () => { if (samples[4]) onSelect(samples[4].id) })
    window.setTimeout(() => mapRef.current?.invalidateSize(), 50)
  }, [center.latitude, center.longitude, locationMode, samples, stale, selectedId, onSelect])

  return <div className="interactive-map" ref={containerRef} aria-label="Mapa interativo com pontos de previsão meteorológica" />
}

function LegendRow({ color, label }: { color: string; label: string }) {
  return <span className="map-legend-row"><i style={{ backgroundColor: color }}/>{label}</span>
}

function WeatherFactor({ label, value }: { label: string; value: string }) {
  return <div className="weather-factor"><small>{label}</small><strong>{value}</strong></div>
}

function formatMm(value: number | null) {
  return value === null ? 'Sem dados' : `${value.toLocaleString('pt-BR', { maximumFractionDigits: 1 })} mm`
}

function formatPercent(value: number | null) {
  return value === null ? 'Sem dados' : `${Math.round(value)}%`
}
