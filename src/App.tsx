import { useCallback, useEffect, useRef, useState, type FormEvent } from 'react'
import { flushSync } from 'react-dom'
import MapSection from './MapSection'
import ThemeToggle from './ThemeToggle'
import Icon from './Icon'
import { occurrences, type Occurrence } from './occurrences'
import SafetyGuidance from './SafetyGuidance'
import { AlertApiError, sendAlert, type AlertInput, type Profile, type ServerAlert } from './api'

type AlertRecord = { id: string; type: Occurrence; date: string; location: string; source: string; profile: Profile; notes: string; confirmedByApi?: boolean; status?: ServerAlert['status'] }
type Screen = 'home' | 'register' | 'profile' | 'history' | 'choose' | 'review' | 'success' | 'detail' | 'guidance'

const PROFILE_KEY = 'sos-localiza-profile'
const ALERTS_KEY = 'sos-localiza-alerts'

function readProfile(): Profile | null {
  try { return JSON.parse(localStorage.getItem(PROFILE_KEY) || 'null') as Profile | null } catch { return null }
}
function readAlerts(): AlertRecord[] {
  try { return JSON.parse(localStorage.getItem(ALERTS_KEY) || '[]') as AlertRecord[] } catch { return [] }
}
function formatDate(value: string) {
  return new Intl.DateTimeFormat('pt-BR', { dateStyle: 'long', timeStyle: 'short' }).format(new Date(value))
}


export default function App() {
  const [profile, setProfile] = useState<Profile | null>(() => readProfile())
  const [alerts, setAlerts] = useState<AlertRecord[]>(() => readAlerts())
  const [requestedScreen, setScreen] = useState<Screen>(() => readProfile() ? 'home' : 'register')
  const [selectedType, setSelectedType] = useState<Occurrence>('Alagamento')
  const [guidanceType, setGuidanceType] = useState<Occurrence | null>(null)
  const [currentAlert, setCurrentAlert] = useState<AlertRecord | null>(null)
  const [activeAlert, setActiveAlert] = useState<AlertRecord | null>(null)
  const [location, setLocation] = useState('')
  const [locationSource, setLocationSource] = useState<AlertInput['source'] | ''>('')
  const [coordinates, setCoordinates] = useState<{ latitude: number; longitude: number } | null>(null)
  const [sending, setSending] = useState(false)
  const pendingAlert = useRef<AlertInput | null>(null)
  const canEditPending = useRef(false)
  const sendInFlight = useRef(false)
  const locationRequest = useRef(0)
  const [locationStatus, setLocationStatus] = useState<'idle' | 'loading' | 'error'>('idle')
  const [manualAddress, setManualAddress] = useState('')
  const [notes, setNotes] = useState('')
  const [formError, setFormError] = useState('')
  const [menuOpen, setMenuOpen] = useState(false)
  const [logoutError, setLogoutError] = useState('')
  const sessionGeneration = useRef(0)
  const focusAfterLogout = useRef(false)
  // Internal screens are never rendered without a local profile.
  const screen: Screen = profile ? requestedScreen : 'register'

  const resetSession = useCallback(() => {
    sessionGeneration.current += 1
    focusAfterLogout.current = true
    setProfile(null)
    setScreen('register')
    setSelectedType('Alagamento')
    setGuidanceType(null)
    setCurrentAlert(null)
    setActiveAlert(null)
    setLocation('')
    setLocationSource('')
    setCoordinates(null)
    locationRequest.current += 1
    pendingAlert.current = null
    canEditPending.current = false
    sendInFlight.current = false
    setSending(false)
    setLocationStatus('idle')
    setManualAddress('')
    setNotes('')
    setFormError('')
    setLogoutError('')
    setMenuOpen(false)
  }, [])

  useEffect(() => {
    function onStorage(event: StorageEvent) {
      try {
        if (event.storageArea === localStorage && (event.key === PROFILE_KEY || event.key === null) && event.newValue === null) {
          flushSync(resetSession)
        }
      } catch { /* Storage access may be blocked; explicit logout reports failure. */ }
    }
    function onPageShow(event: PageTransitionEvent) {
      if (!event.persisted) return
      try {
        if (localStorage.getItem(PROFILE_KEY) !== JSON.stringify(profile)) flushSync(resetSession)
      } catch { /* Retain the in-memory session if storage cannot be checked. */ }
    }
    window.addEventListener('storage', onStorage)
    window.addEventListener('pageshow', onPageShow)
    return () => {
      window.removeEventListener('storage', onStorage)
      window.removeEventListener('pageshow', onPageShow)
    }
  }, [resetSession, profile])

  useEffect(() => {
    if (!profile && focusAfterLogout.current) {
      focusAfterLogout.current = false
      document.querySelector<HTMLInputElement>('input[name="name"]')?.focus()
    }
  }, [profile])

  useEffect(() => { try { localStorage.setItem(ALERTS_KEY, JSON.stringify(alerts)) } catch { /* Keep records in memory for this session. */ } }, [alerts])

  function navigate(next: Screen) {
    if (sendInFlight.current) return
    if (next !== 'choose' && locationStatus === 'loading') { locationRequest.current += 1; setLocationStatus('idle') }
    if (pendingAlert.current && !canEditPending.current && (next === 'choose' || next === 'register')) {
      setScreen('review'); setFormError('Há um envio sem confirmação. Tente novamente antes de alterar os dados.'); return
    }
    setScreen(profile || next === 'register' ? next : 'register'); setMenuOpen(false); setFormError('')
  }
  function saveProfile(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const data = new FormData(event.currentTarget)
    const next: Profile = { name: String(data.get('name')).trim(), cpf: String(data.get('cpf')).trim(), phone: String(data.get('phone')).trim(), address: String(data.get('address')).trim() }
    if (!next.name || !next.cpf || !next.phone || !next.address) { setFormError('Preencha todos os campos para continuar.'); return }
    // Persist only an explicit save, so a restored tab cannot resurrect a logged-out profile.
    try { localStorage.setItem(PROFILE_KEY, JSON.stringify(next)) } catch { /* Keep the existing session-only fallback. */ }
    sessionGeneration.current += 1
    setProfile(next); setScreen('home'); setMenuOpen(false); setFormError(''); setLogoutError('')
  }
  function logout() {
    try {
      localStorage.removeItem(PROFILE_KEY)
      if (localStorage.getItem(PROFILE_KEY) !== null) throw new Error('Profile was not removed')
    } catch {
      setLogoutError('Não foi possível sair porque o navegador bloqueou a remoção do cadastro local. Verifique as permissões de armazenamento e tente novamente.')
      return
    }
    resetSession()
  }
  function beginAlert() {
    if (pendingAlert.current) { navigate(canEditPending.current ? 'choose' : 'review'); return }
    locationRequest.current += 1
    setCoordinates(null)
    setLocation(''); setLocationSource(''); setManualAddress(''); setLocationStatus('idle'); setNotes(''); navigate('choose')
  }
  function requestLocation() {
    if (!('geolocation' in navigator)) { setLocationStatus('error'); return }
    setLocationStatus('loading')
    const generation = sessionGeneration.current
    const request = ++locationRequest.current
    navigator.geolocation.getCurrentPosition(
      ({ coords }) => { if (generation !== sessionGeneration.current || request !== locationRequest.current) return; setCoordinates({ latitude: coords.latitude, longitude: coords.longitude }); setLocation(`${coords.latitude.toFixed(5)}, ${coords.longitude.toFixed(5)}`); setLocationSource('Localização do dispositivo'); setLocationStatus('idle') },
      () => { if (generation === sessionGeneration.current && request === locationRequest.current) setLocationStatus('error') },
      { enableHighAccuracy: false, timeout: 9000, maximumAge: 60000 },
    )
  }
  function continueToReview() {
    if (locationStatus === 'loading') return
    if (!profile) { navigate('register'); return }
    const deviceLocation = locationSource === 'Localização do dispositivo' && coordinates
    const place = deviceLocation ? location : manualAddress.trim() || profile.address
    const source = deviceLocation ? 'Localização do dispositivo' : manualAddress.trim() ? 'Informada manualmente' : 'Endereço cadastrado'
    setLocation(place); setLocationSource(source); navigate('review')
  }
  async function confirmAlert() {
    if (!profile || sendInFlight.current) return
    const generation = sessionGeneration.current
    if (!pendingAlert.current || canEditPending.current) {
      pendingAlert.current = {
        requestId: pendingAlert.current?.requestId || crypto.randomUUID(), type: selectedType, location: location || profile.address,
        source: locationSource || 'Endereço cadastrado',
        latitude: locationSource === 'Localização do dispositivo' ? coordinates?.latitude ?? null : null,
        longitude: locationSource === 'Localização do dispositivo' ? coordinates?.longitude ?? null : null,
        profile: { ...profile }, notes: notes.trim(),
      }
      canEditPending.current = false
    }
    sendInFlight.current = true
    setSending(true); setFormError('')
    try {
      const saved = await sendAlert(pendingAlert.current)
      if (generation !== sessionGeneration.current) return
      const record: AlertRecord = { ...saved, confirmedByApi: true }
      setAlerts((current) => [record, ...current.filter((item) => item.id !== record.id)])
      setCurrentAlert(record)
      pendingAlert.current = null
      setScreen('success')
    } catch (error) {
      if (generation === sessionGeneration.current) {
        // Validation responses guarantee no write; edits may retain the same logical requestId.
        if (error instanceof AlertApiError && [400, 413].includes(error.status)) canEditPending.current = true
        setFormError(error instanceof Error ? error.message : 'Não foi possível confirmar o envio. Tente novamente.')
      }
    } finally {
      if (generation === sessionGeneration.current) { sendInFlight.current = false; setSending(false) }
    }
  }
  function viewAlert(record: AlertRecord) { setActiveAlert(record); navigate('detail') }
  function openGuidance(type: Occurrence | null = null) {
    if (!profile) return
    setGuidanceType(type); navigate('guidance')
  }
  const title = screen === 'guidance' ? 'Orientações de Segurança' : screen === 'history' ? 'Histórico de alertas' : screen === 'profile' ? 'Meus dados' : screen === 'choose' ? 'O que está acontecendo?' : screen === 'review' ? 'Revise antes de registrar' : screen === 'success' ? 'Registro concluído' : screen === 'detail' ? 'Detalhes do registro' : screen === 'register' ? (profile ? 'Atualizar cadastro' : 'Vamos começar?') : 'Olá, ' + (profile?.name.split(' ')[0] || 'por aqui')
  const showNav = !!profile && !['register', 'choose', 'review', 'success', 'detail'].includes(screen)

  return <div className="app-shell">
    <header className="topbar">
      <button className="brand" onClick={() => navigate(profile ? 'home' : 'register')} aria-label="Ir para início"><span className="brand-mark"><Icon name="logo" size={25} /></span><span className="brand-copy"><strong>SOS <em>LOCALIZA</em></strong><small>cuidado perto de você</small></span></button>
      <div className="top-actions"><ThemeToggle/>{profile && <button className="avatar" onClick={() => navigate('profile')} aria-label="Abrir perfil">{profile.name.charAt(0).toUpperCase()}</button>}<button className="menu-toggle" onClick={() => setMenuOpen(!menuOpen)} aria-label="Abrir menu"><span/><span/><span/></button></div>
    </header>
    {showNav && <nav className={`desktop-nav ${menuOpen ? 'nav-open' : ''}`} aria-label="Navegação principal"><button className={screen === 'home' ? 'active' : ''} onClick={() => navigate('home')}><Icon name="home" size={18}/> Início</button><button className={screen === 'history' ? 'active' : ''} onClick={() => navigate('history')}><Icon name="history" size={18}/> Histórico {alerts.length > 0 && <span className="nav-count">{alerts.length}</span>}</button><button className={screen === 'guidance' ? 'active' : ''} aria-current={screen === 'guidance' ? 'page' : undefined} onClick={() => openGuidance()}><Icon name="guide" size={18}/>Orientações</button><button className={screen === 'profile' ? 'active' : ''} onClick={() => navigate('profile')}><Icon name="user" size={18}/> Meus dados</button></nav>}
    <main className="main-content">
      {screen !== 'home' && screen !== 'register' && <div className="page-heading"><div>{['choose', 'review', 'detail'].includes(screen) && <button className="back-button" disabled={sending || (screen === 'review' && !!pendingAlert.current && !canEditPending.current)} onClick={() => navigate(screen === 'review' ? 'choose' : screen === 'detail' ? 'history' : 'home')}><Icon name="back" size={18}/> Voltar</button>}<h1>{title}</h1></div></div>}

      {screen === 'guidance' && profile && <SafetyGuidance selected={guidanceType} onSelect={setGuidanceType} onHome={() => navigate('home')}/>}

      {screen === 'home' && profile && <>
        <section className="home-intro" aria-labelledby="home-title"><div className="home-intro-copy"><h1 id="home-title">Atenção ao seu entorno</h1><p>Consulte a previsão próxima e registre uma ocorrência.</p></div><button className="primary-button alert-button" onClick={beginAlert}><span className="button-icon"><Icon name="alert" size={22}/></span><span><strong>Comunicar estado de alerta</strong><small>Registrar ocorrência</small></span><Icon name="arrow" size={21}/></button></section>
        <div className="home-main-grid">
          <MapSection/>
          <aside className="home-recent" aria-labelledby="recent-title">
            <div className="home-recent-heading"><h2 id="recent-title">Registros recentes</h2><button className="home-history-link" onClick={() => navigate('history')}>Ver histórico <Icon name="arrow" size={15}/></button></div>
            {alerts.length > 0 ? <ul className="home-recent-list">{alerts.slice(0, 4).map((record) => <li key={record.id}><button className="home-recent-item" onClick={() => viewAlert(record)}><span className="home-recent-type">{record.type}</span><span className="home-recent-meta">{new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short', timeStyle: 'short' }).format(new Date(record.date))} · {record.location}</span><span className="home-recent-tag">{record.confirmedByApi ? 'Confirmado pela API' : 'Legado local — sem envio'}</span></button></li>)}</ul> : <p className="home-recent-empty">Nenhuma ocorrência registrada neste dispositivo.</p>}
            <p className="home-storage-note"><Icon name="lock" size={15}/> Histórico neste dispositivo; novos alertas são enviados à Central.</p>
          </aside>
        </div>
      </>}

      {screen === 'register' && <section className="auth-layout"><div className="auth-story"><h1>Informação que <br/><span>pode fazer diferença.</span></h1><p>Cadastre seus dados para registrar situações de risco e consultar suas ocorrências.</p><div className="story-badge"><span className="quick-icon teal"><Icon name="lock"/></span><span><strong>Privacidade em primeiro lugar</strong><small>Ao enviar um alerta, os dados seguem para a Central.</small></span></div></div><form key={profile ? 'edit-profile' : 'new-profile'} className="form-card" onSubmit={saveProfile}><div className="form-heading"><h2>{profile ? 'Atualize seu cadastro' : 'Seus dados de contato'}</h2><p>O cadastro é local. Uma cópia dos dados acompanha cada alerta enviado à Central.</p></div><label>Nome completo<input name="name" maxLength={120} autoComplete="name" placeholder="Ex.: Alex Silva" defaultValue={profile?.name || ''} required/></label><div className="form-row"><label>CPF<input name="cpf" maxLength={20} inputMode="numeric" placeholder="000.000.000-00" defaultValue={profile?.cpf || ''} required/></label><label>Telefone<input name="phone" maxLength={30} type="tel" autoComplete="tel" placeholder="(00) 00000-0000" defaultValue={profile?.phone || ''} required/></label></div><label>Endereço residencial<input name="address" maxLength={500} autoComplete="street-address" placeholder="Rua, número, bairro e cidade" defaultValue={profile?.address || ''} required/></label>{formError && <p className="form-error">{formError}</p>}<p className="form-privacy"><Icon name="lock" size={15}/> Cadastro local; dados de contato são enviados à Central quando você confirma um alerta.</p><button className="primary-button wide" type="submit">{profile ? 'Salvar alterações' : 'Continuar'} <Icon name="arrow" size={19}/></button></form></section>}

      {screen === 'profile' && profile && <section className="content-narrow"><div className="profile-card"><div className="profile-banner"><div className="profile-avatar">{profile.name.charAt(0).toUpperCase()}</div><div><h2>{profile.name}</h2></div><button className="secondary-button edit-profile" onClick={() => navigate('register')}><Icon name="edit" size={17}/> Editar</button></div><div className="profile-fields"><ProfileField label="CPF" value={profile.cpf}/><ProfileField label="Telefone" value={profile.phone}/><ProfileField label="Endereço residencial" value={profile.address}/></div></div><div className="profile-session-actions"><button className="secondary-button logout-button" onClick={logout}><Icon name="logout" size={20}/> Sair</button>{logoutError && <p className="logout-error" role="alert">{logoutError}</p>}</div><div className="privacy-card"><span className="quick-icon teal"><Icon name="lock"/></span><div><strong>Cadastro local e alertas na Central</strong><p>Seu cadastro permanece no navegador. Ao confirmar um alerta, seus dados de contato, CPF, endereço, localização e observações são enviados à API da Central e armazenados no Oracle. O histórico é uma cópia local e não acompanha atualizações do atendimento. Para consultar a previsão, o mapa envia coordenadas à Open-Meteo e carrega mapas do OpenStreetMap.</p></div></div></section>}

      {screen === 'choose' && <section className="flow-layout"><div className="flow-main"><div className="flow-progress"><span className="progress-active"/><span/><span/></div><p className="step-description">Selecione a situação que mais se aproxima do que você está vendo.</p><div className="occurrence-list">{occurrences.map((item, index) => <button key={item.label} className={`occurrence-option ${selectedType === item.label ? 'selected' : ''}`} onClick={() => setSelectedType(item.label)}><span className={`occ-icon occ-${index}`}><Icon name={item.icon} size={23}/></span><span className="occ-copy"><strong>{item.label}</strong><small>{item.hint}</small></span><span className="radio-dot"/></button>)}</div>{selectedType === 'Outra situação' && <label className="notes-field">Conte um pouco mais (opcional)<textarea maxLength={2000} value={notes} onChange={(e) => setNotes(e.target.value)} placeholder="Descreva brevemente o que está acontecendo" rows={3}/></label>}<div className="location-panel"><div className="location-heading"><span className="quick-icon teal"><Icon name="pin"/></span><span><strong>Onde está acontecendo?</strong><small>Compartilhe sua localização ou informe um endereço.</small></span></div>{location && locationSource === 'Localização do dispositivo' ? <div className="location-found"><Icon name="checkCircle" size={18}/><span><strong>Localização encontrada</strong><small>{location}</small></span><button onClick={() => {setCoordinates(null);setLocation('');setLocationSource('')}}>Alterar</button></div> : <><button className="location-button" onClick={requestLocation} disabled={locationStatus === 'loading'}><Icon name="pin" size={18}/>{locationStatus === 'loading' ? 'Buscando localização…' : 'Usar localização do dispositivo'}</button>{locationStatus === 'error' && <p className="location-error">Não foi possível acessar a localização. Você pode informar um endereço abaixo ou usar seu endereço cadastrado.</p>}<label className="manual-location">Ou digite um endereço para esta ocorrência<input maxLength={500} value={manualAddress} onChange={(e) => setManualAddress(e.target.value)} placeholder="Rua, bairro, cidade"/></label><p className="location-fallback"><Icon name="check" size={15}/> Se preferir, usaremos seu endereço cadastrado: <strong>{profile?.address}</strong></p></>}</div><button className="primary-button wide flow-continue" disabled={locationStatus === 'loading'} onClick={continueToReview}>Continuar para revisão <Icon name="arrow" size={19}/></button></div><aside className="flow-aside"><div className="aside-symbol"><Icon name="logo" size={32}/></div><h3>Sua segurança vem primeiro.</h3><p>Se estiver em perigo imediato, afaste-se da área de risco e procure os canais oficiais de emergência.</p></aside></section>}

      {screen === 'review' && profile && <section className="flow-layout"><div className="flow-main"><div className="flow-progress"><span className="progress-active"/><span className="progress-active"/><span/></div><p className="step-description">Confira as informações. Ao confirmar, seus dados e a ocorrência serão enviados à Central. O sucesso só aparece após a API confirmar a gravação no Oracle.</p><div className="review-card"><div className="review-section"><span className="review-label">SITUAÇÃO INFORMADA</span><div className="review-type"><span className="occ-icon occ-0"><Icon name={occurrences.find(x=>x.label===selectedType)?.icon || 'alert'}/></span><strong>{selectedType}</strong></div>{notes && <p className="review-notes">{notes}</p>}</div><div className="review-divider"/><div className="review-section"><span className="review-label">DADOS PARA CONTATO</span><ReviewRow label="Nome" value={profile.name}/><ReviewRow label="Telefone" value={profile.phone}/><ReviewRow label="Endereço cadastrado" value={profile.address}/></div><div className="review-divider"/><div className="review-section"><span className="review-label">LOCAL DA OCORRÊNCIA</span><div className="review-location"><Icon name="pin" size={18}/><span><strong>{location || profile.address}</strong><small>{locationSource || 'Endereço cadastrado'}</small></span></div></div></div><div aria-live="polite" aria-busy={sending}>{formError && <p className="form-error" role="alert">{formError}</p>}{pendingAlert.current && !sending && <p className="step-description">{canEditPending.current ? 'A API recusou os dados sem gravar. Volte para editar e tente novamente.' : 'Esta tentativa mantém os mesmos dados e identificador. Tente novamente para recuperar a confirmação sem duplicar o alerta.'}</p>}</div><div className="review-actions"><button disabled={sending || (!!pendingAlert.current && !canEditPending.current)} className="secondary-button" onClick={() => navigate('choose')}><Icon name="back" size={18}/> Voltar e editar</button><button className="primary-button" disabled={sending} onClick={confirmAlert}>{sending ? 'Enviando…' : pendingAlert.current ? 'Tentar novamente' : 'Confirmar registro'} <Icon name="arrow" size={19}/></button></div></div><aside className="flow-aside"><div className="aside-symbol"><Icon name="lock" size={30}/></div><h3>Envio para a Central SOS Localiza.</h3><p>A API armazena o alerta no Oracle e uma cópia confirmada fica no histórico deste navegador. Isso não substitui os canais oficiais de emergência.</p></aside></section>}

      {screen === 'success' && currentAlert && <section className="success-wrap"><div className="success-card"><div className="success-mark"><Icon name="check" size={34}/></div><h2>Alerta salvo na Central SOS Localiza.</h2><p className="success-lead">A API confirmou a gravação no Oracle. A equipe da Defesa Civil não foi notificada por este aplicativo.</p><div className="success-id"><span>IDENTIFICADOR DO REGISTRO</span><strong>{currentAlert.id}</strong><small>{formatDate(currentAlert.date)}</small></div><button className="primary-button success-guidance" onClick={() => openGuidance(currentAlert.type)}><Icon name="guide" size={20}/> Ver orientações de segurança</button><div className="success-actions"><button className="secondary-button" onClick={() => navigate('home')}><Icon name="home" size={18}/> Voltar ao início</button><button className="secondary-button" onClick={() => navigate('history')}><Icon name="history" size={18}/> Consultar histórico</button></div><p className="real-emergency">Em perigo imediato, procure os canais oficiais de emergência.</p></div></section>}

      {screen === 'history' && <section className="content-narrow"><div className="history-toolbar"><p>{alerts.length ? `${alerts.length} registro${alerts.length > 1 ? 's' : ''} salvo${alerts.length > 1 ? 's' : ''} neste dispositivo` : 'Seus registros aparecerão aqui.'}</p><button className="secondary-button" onClick={beginAlert}><Icon name="plus" size={17}/> Novo alerta</button></div>{alerts.length === 0 ? <div className="empty-state"><span className="empty-icon"><Icon name="history" size={31}/></span><h2>Nenhum alerta por aqui</h2><p>Quando você registrar uma ocorrência, ela aparecerá nesta lista.</p><button className="primary-button" onClick={beginAlert}>Registrar um alerta <Icon name="arrow" size={18}/></button></div> : <div className="history-list">{alerts.map((alert) => <button className="history-item" key={alert.id} onClick={() => viewAlert(alert)}><span className="history-type-icon"><Icon name={occurrences.find(x=>x.label===alert.type)?.icon || 'alert'}/></span><span className="history-info"><strong>{alert.type}</strong><small><Icon name="clock" size={14}/>{formatDate(alert.date)}</small><small><Icon name="pin" size={14}/>{alert.location}</small></span><span className="record-tag"><i/> {alert.confirmedByApi ? 'Confirmado pela API' : 'Legado local — sem envio'}</span><Icon name="arrow" size={18} className="history-arrow"/></button>)}</div>}</section>}

      {screen === 'detail' && activeAlert && <section className="content-narrow"><div className="detail-card"><div className="detail-top"><span className="occ-icon occ-0"><Icon name={occurrences.find(x=>x.label===activeAlert.type)?.icon || 'alert'} size={25}/></span><div><h2>{activeAlert.type}</h2></div><span className="record-tag"><i/> {activeAlert.confirmedByApi ? 'Confirmado pela API' : 'Legado local — sem envio'}</span></div><div className="detail-grid">{activeAlert.status && <ReviewRow label="Status no momento do envio (cópia local)" value={activeAlert.status}/>}<ReviewRow label="Identificador do registro" value={activeAlert.id}/><ReviewRow label="Data e horário" value={formatDate(activeAlert.date)}/><ReviewRow label="Localização informada" value={activeAlert.location}/><ReviewRow label="Origem da localização" value={activeAlert.source}/><ReviewRow label="Nome" value={activeAlert.profile.name}/><ReviewRow label="Telefone" value={activeAlert.profile.phone}/>{activeAlert.notes && <ReviewRow label="Observação" value={activeAlert.notes}/>}</div></div><button className="secondary-button back-history" onClick={() => navigate('history')}><Icon name="back" size={18}/> Voltar ao histórico</button></section>}
    </main>
    {showNav && <nav className="mobile-nav" aria-label="Navegação inferior"><button className={screen === 'home' ? 'active' : ''} onClick={() => navigate('home')}><Icon name="home" size={20}/>Início</button><button className={screen === 'history' ? 'active' : ''} onClick={() => navigate('history')}><Icon name="history" size={20}/>Histórico</button><button className={screen === 'guidance' ? 'active' : ''} aria-current={screen === 'guidance' ? 'page' : undefined} onClick={() => openGuidance()}><Icon name="guide" size={20}/>Orientações</button><button className={screen === 'profile' ? 'active' : ''} onClick={() => navigate('profile')}><Icon name="user" size={20}/>Perfil</button></nav>}
    <footer className="site-footer"><span>© SOS LOCALIZA</span></footer>
  </div>
}

function ProfileField({ label, value }: { label: string; value: string }) { return <div className="profile-field"><span>{label}</span><strong>{value}</strong></div> }
function ReviewRow({ label, value }: { label: string; value: string }) { return <div className="review-row"><span>{label}</span><strong>{value}</strong></div> }
