import { useEffect, useState, type FormEvent, type ReactNode } from 'react'
import MapSection from './MapSection'
import ThemeToggle from './ThemeToggle'

type Profile = { name: string; cpf: string; phone: string; address: string }
type Occurrence = 'Alagamento' | 'Enchente' | 'Desmoronamento ou barranco em risco' | 'Casa com risco de desabamento' | 'Outra situação'
type AlertRecord = { id: string; type: Occurrence; date: string; location: string; source: string; profile: Profile; notes: string }
type Screen = 'home' | 'register' | 'profile' | 'history' | 'choose' | 'review' | 'success' | 'detail'

const PROFILE_KEY = 'sos-localiza-profile'
const ALERTS_KEY = 'sos-localiza-alerts'
const occurrences: { label: Occurrence; icon: string; hint: string }[] = [
  { label: 'Alagamento', icon: 'water', hint: 'Água acumulada em ruas ou áreas próximas' },
  { label: 'Enchente', icon: 'waves', hint: 'Elevação ou transbordamento de água' },
  { label: 'Desmoronamento ou barranco em risco', icon: 'mountain', hint: 'Terra cedendo ou risco de deslizamento' },
  { label: 'Casa com risco de desabamento', icon: 'house', hint: 'Danos estruturais ou risco na moradia' },
  { label: 'Outra situação', icon: 'plus', hint: 'Descreva uma situação diferente' },
]

function readProfile(): Profile | null {
  try { return JSON.parse(localStorage.getItem(PROFILE_KEY) || 'null') as Profile | null } catch { return null }
}
function readAlerts(): AlertRecord[] {
  try { return JSON.parse(localStorage.getItem(ALERTS_KEY) || '[]') as AlertRecord[] } catch { return [] }
}
function formatDate(value: string) {
  return new Intl.DateTimeFormat('pt-BR', { dateStyle: 'long', timeStyle: 'short' }).format(new Date(value))
}

function Icon({ name, size = 22, className }: { name: string; size?: number; className?: string }) {
  const common = { width: size, height: size, viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', strokeWidth: 1.8, strokeLinecap: 'round' as const, strokeLinejoin: 'round' as const, 'aria-hidden': true as const }
  const paths: Record<string, ReactNode> = {
    logo: <><path d="M12 22s8-7 8-13a8 8 0 1 0-16 0c0 6 8 13 8 13Z"/><path d="m12 7 4 1.5v3.2c0 2.2-2.1 3.9-4 4.8-1.9-.9-4-2.6-4-4.8V8.5L12 7Z"/><path d="m10.5 11.3 1.1 1.1 2-2.2"/></>,
    home: <><path d="m3 10 9-7 9 7"/><path d="M5 9v11h14V9M9 20v-6h6v6"/></>,
    history: <><path d="M3 12a9 9 0 1 0 2.6-6.4L3 8"/><path d="M3 3v5h5M12 7v5l3 2"/></>,
    user: <><circle cx="12" cy="8" r="4"/><path d="M4 21a8 8 0 0 1 16 0"/></>,
    pin: <><path d="M20 10c0 5-8 12-8 12S4 15 4 10a8 8 0 1 1 16 0Z"/><circle cx="12" cy="10" r="2.5"/></>,
    arrow: <><path d="M5 12h14M13 6l6 6-6 6"/></>,
    back: <><path d="M19 12H5m6 7-7-7 7-7"/></>,
    check: <path d="m5 12 4 4L19 6"/>,
    close: <><path d="m18 6-12 12M6 6l12 12"/></>,
    water: <><path d="M12 3c-3 4-6 7-6 11a6 6 0 0 0 12 0c0-4-3-7-6-11Z"/><path d="M9 15c.4 1.2 1.3 1.8 2.5 2"/></>,
    waves: <><path d="M3 8c2 0 2-2 4-2s2 2 4 2 2-2 4-2 2 2 4 2 2-2 4-2M3 14c2 0 2-2 4-2s2 2 4 2 2-2 4-2 2 2 4 2 2-2 4-2M3 20c2 0 2-2 4-2s2 2 4 2 2-2 4-2 2 2 4 2 2-2 4-2"/></>,
    mountain: <><path d="m3 20 7-14 4 8 2-4 5 10H3Z"/><path d="m8 10 2 2 2-1"/></>,
    house: <><path d="m3 10 9-7 9 7"/><path d="M5 9v11h14V9M9 20v-6h6v6"/><path d="M16 5v3"/></>,
    plus: <><path d="M12 5v14M5 12h14"/></>,
    alert: <><path d="M10.3 3.9 2.5 17.4A2 2 0 0 0 4.2 20h15.6a2 2 0 0 0 1.7-2.6L13.7 3.9a2 2 0 0 0-3.4 0Z"/><path d="M12 9v4m0 4h.01"/></>,
    lock: <><rect x="4" y="10" width="16" height="11" rx="2"/><path d="M8 10V7a4 4 0 0 1 8 0v3"/></>,
    clock: <><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/></>,
    checkCircle: <><circle cx="12" cy="12" r="9"/><path d="m8 12 2.5 2.5L16 9"/></>,
    edit: <><path d="M12 20h9"/><path d="M16.5 3.5a2.1 2.1 0 0 1 3 3L8 18l-4 1 1-4Z"/></>,
  }
  return <svg {...common} className={className}>{paths[name] || paths.plus}</svg>
}

export default function App() {
  const [profile, setProfile] = useState<Profile | null>(() => readProfile())
  const [alerts, setAlerts] = useState<AlertRecord[]>(() => readAlerts())
  const [screen, setScreen] = useState<Screen>(() => readProfile() ? 'home' : 'register')
  const [selectedType, setSelectedType] = useState<Occurrence>('Alagamento')
  const [currentAlert, setCurrentAlert] = useState<AlertRecord | null>(null)
  const [activeAlert, setActiveAlert] = useState<AlertRecord | null>(null)
  const [location, setLocation] = useState('')
  const [locationSource, setLocationSource] = useState('')
  const [locationStatus, setLocationStatus] = useState<'idle' | 'loading' | 'error'>('idle')
  const [manualAddress, setManualAddress] = useState('')
  const [notes, setNotes] = useState('')
  const [formError, setFormError] = useState('')
  const [menuOpen, setMenuOpen] = useState(false)

  useEffect(() => { try { if (profile) localStorage.setItem(PROFILE_KEY, JSON.stringify(profile)) } catch { /* Storage may be blocked by the browser. */ } }, [profile])
  useEffect(() => { try { localStorage.setItem(ALERTS_KEY, JSON.stringify(alerts)) } catch { /* Keep records in memory for this session. */ } }, [alerts])

  function navigate(next: Screen) { setScreen(next); setMenuOpen(false); setFormError('') }
  function saveProfile(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const data = new FormData(event.currentTarget)
    const next: Profile = { name: String(data.get('name')).trim(), cpf: String(data.get('cpf')).trim(), phone: String(data.get('phone')).trim(), address: String(data.get('address')).trim() }
    if (!next.name || !next.cpf || !next.phone || !next.address) { setFormError('Preencha todos os campos para continuar.'); return }
    setProfile(next); navigate('home')
  }
  function beginAlert() {
    setLocation(''); setLocationSource(''); setManualAddress(''); setLocationStatus('idle'); setNotes(''); navigate('choose')
  }
  function requestLocation() {
    if (!('geolocation' in navigator)) { setLocationStatus('error'); return }
    setLocationStatus('loading')
    navigator.geolocation.getCurrentPosition(
      ({ coords }) => { setLocation(`${coords.latitude.toFixed(5)}, ${coords.longitude.toFixed(5)}`); setLocationSource('Localização do dispositivo'); setLocationStatus('idle') },
      () => { setLocationStatus('error') },
      { enableHighAccuracy: false, timeout: 9000, maximumAge: 60000 },
    )
  }
  function continueToReview() {
    if (!profile) { navigate('register'); return }
    const place = location || manualAddress.trim() || profile.address
    const source = location ? locationSource : manualAddress.trim() ? 'Informada manualmente' : 'Endereço cadastrado'
    setLocation(place); setLocationSource(source); navigate('review')
  }
  function confirmAlert() {
    if (!profile) return
    const record: AlertRecord = { id: `SL-${Math.random().toString(36).slice(2, 8).toUpperCase()}`, type: selectedType, date: new Date().toISOString(), location: location || profile.address, source: locationSource || 'Endereço cadastrado', profile, notes }
    setAlerts((current) => [record, ...current]); setCurrentAlert(record); navigate('success')
  }
  function viewAlert(record: AlertRecord) { setActiveAlert(record); navigate('detail') }
  const title = screen === 'history' ? 'Histórico de alertas' : screen === 'profile' ? 'Meus dados' : screen === 'choose' ? 'O que está acontecendo?' : screen === 'review' ? 'Revise antes de registrar' : screen === 'success' ? 'Registro concluído' : screen === 'detail' ? 'Detalhes do registro' : screen === 'register' ? (profile ? 'Atualizar cadastro' : 'Vamos começar?') : 'Olá, ' + (profile?.name.split(' ')[0] || 'por aqui')
  const showNav = !!profile && !['register', 'choose', 'review', 'success', 'detail'].includes(screen)

  return <div className="app-shell">
    <header className="topbar">
      <button className="brand" onClick={() => navigate(profile ? 'home' : 'register')} aria-label="Ir para início"><span className="brand-mark"><Icon name="logo" size={25} /></span><span className="brand-copy"><strong>SOS <em>LOCALIZA</em></strong><small>cuidado perto de você</small></span></button>
      <div className="top-actions"><ThemeToggle/><span className="prototype-chip"><span /> Protótipo acadêmico</span>{profile && <button className="avatar" onClick={() => navigate('profile')} aria-label="Abrir perfil">{profile.name.charAt(0).toUpperCase()}</button>}<button className="menu-toggle" onClick={() => setMenuOpen(!menuOpen)} aria-label="Abrir menu"><span/><span/><span/></button></div>
    </header>
    {showNav && <nav className={`desktop-nav ${menuOpen ? 'nav-open' : ''}`} aria-label="Navegação principal"><button className={screen === 'home' ? 'active' : ''} onClick={() => navigate('home')}><Icon name="home" size={18}/> Início</button><button className={screen === 'history' ? 'active' : ''} onClick={() => navigate('history')}><Icon name="history" size={18}/> Histórico {alerts.length > 0 && <span className="nav-count">{alerts.length}</span>}</button><button className={screen === 'profile' ? 'active' : ''} onClick={() => navigate('profile')}><Icon name="user" size={18}/> Meus dados</button></nav>}
    <main className="main-content">
      {screen !== 'home' && screen !== 'register' && <div className="page-heading"><div>{['choose', 'review', 'detail'].includes(screen) && <button className="back-button" onClick={() => navigate(screen === 'review' ? 'choose' : screen === 'detail' ? 'history' : 'home')}><Icon name="back" size={18}/> Voltar</button>}<h1>{title}</h1></div></div>}

      {screen === 'home' && profile && <>
        <section className="home-intro" aria-labelledby="home-title"><div className="home-intro-copy"><h1 id="home-title">Atenção ao seu entorno</h1><p>Consulte a previsão próxima e registre uma ocorrência de demonstração.</p></div><button className="primary-button alert-button" onClick={beginAlert}><span className="button-icon"><Icon name="alert" size={22}/></span><span><strong>Comunicar estado de alerta</strong><small>Registrar ocorrência simulada</small></span><Icon name="arrow" size={21}/></button></section>
        <div className="home-main-grid">
          <MapSection/>
          <aside className="home-recent" aria-labelledby="recent-title">
            <div className="home-recent-heading"><h2 id="recent-title">Registros recentes</h2><button className="home-history-link" onClick={() => navigate('history')}>Ver histórico <Icon name="arrow" size={15}/></button></div>
            {alerts.length > 0 ? <ul className="home-recent-list">{alerts.slice(0, 4).map((record) => <li key={record.id}><button className="home-recent-item" onClick={() => viewAlert(record)}><span className="home-recent-type">{record.type}</span><span className="home-recent-meta">{new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short', timeStyle: 'short' }).format(new Date(record.date))} · {record.location}</span><span className="home-recent-tag">Simulado</span></button></li>)}</ul> : <p className="home-recent-empty">Nenhuma ocorrência registrada neste dispositivo.</p>}
            <p className="home-prototype-note"><Icon name="lock" size={15}/> Protótipo acadêmico. Seus registros ficam neste dispositivo.</p>
          </aside>
        </div>
      </>}

      {screen === 'register' && <section className="auth-layout"><div className="auth-story"><h1>Informação que <br/><span>pode fazer diferença.</span></h1><p>Cadastre seus dados para experimentar o fluxo de comunicação de uma situação de risco.</p><div className="story-badge"><span className="quick-icon teal"><Icon name="lock"/></span><span><strong>Privacidade em primeiro lugar</strong><small>Os dados permanecem neste navegador.</small></span></div></div><form className="form-card" onSubmit={saveProfile}><div className="form-heading"><h2>{profile ? 'Atualize seu cadastro' : 'Seus dados de contato'}</h2><p>Usaremos estas informações apenas para montar a prévia do alerta.</p></div><label>Nome completo<input name="name" autoComplete="name" placeholder="Ex.: Alex Silva" defaultValue={profile?.name || ''} required/></label><div className="form-row"><label>CPF<input name="cpf" inputMode="numeric" placeholder="000.000.000-00" defaultValue={profile?.cpf || ''} required/></label><label>Telefone<input name="phone" type="tel" autoComplete="tel" placeholder="(00) 00000-0000" defaultValue={profile?.phone || ''} required/></label></div><label>Endereço residencial<input name="address" autoComplete="street-address" placeholder="Rua, número, bairro e cidade" defaultValue={profile?.address || ''} required/></label>{formError && <p className="form-error">{formError}</p>}<p className="form-privacy"><Icon name="lock" size={15}/> Não use seus dados reais nesta demonstração.</p><button className="primary-button wide" type="submit">{profile ? 'Salvar alterações' : 'Continuar'} <Icon name="arrow" size={19}/></button></form></section>}

      {screen === 'profile' && profile && <section className="content-narrow"><div className="profile-card"><div className="profile-banner"><div className="profile-avatar">{profile.name.charAt(0).toUpperCase()}</div><div><h2>{profile.name}</h2></div><button className="secondary-button edit-profile" onClick={() => navigate('register')}><Icon name="edit" size={17}/> Editar</button></div><div className="profile-fields"><ProfileField label="CPF" value={profile.cpf}/><ProfileField label="Telefone" value={profile.phone}/><ProfileField label="Endereço residencial" value={profile.address}/></div></div><div className="privacy-card"><span className="quick-icon teal"><Icon name="lock"/></span><div><strong>Seus dados ficam neste dispositivo</strong><p>O cadastro é usado somente para montar as telas de demonstração. Nada é transmitido para serviços externos.</p></div></div><DemoNotice/></section>}

      {screen === 'choose' && <section className="flow-layout"><div className="flow-main"><div className="flow-progress"><span className="progress-active"/><span/><span/></div><p className="step-description">Selecione a situação que mais se aproxima do que você está vendo.</p><div className="occurrence-list">{occurrences.map((item, index) => <button key={item.label} className={`occurrence-option ${selectedType === item.label ? 'selected' : ''}`} onClick={() => setSelectedType(item.label)}><span className={`occ-icon occ-${index}`}><Icon name={item.icon} size={23}/></span><span className="occ-copy"><strong>{item.label}</strong><small>{item.hint}</small></span><span className="radio-dot"/></button>)}</div>{selectedType === 'Outra situação' && <label className="notes-field">Conte um pouco mais (opcional)<textarea value={notes} onChange={(e) => setNotes(e.target.value)} placeholder="Descreva brevemente o que está acontecendo" rows={3}/></label>}<div className="location-panel"><div className="location-heading"><span className="quick-icon teal"><Icon name="pin"/></span><span><strong>Onde está acontecendo?</strong><small>Compartilhe sua localização ou informe um endereço.</small></span></div>{location && locationSource === 'Localização do dispositivo' ? <div className="location-found"><Icon name="checkCircle" size={18}/><span><strong>Localização encontrada</strong><small>{location}</small></span><button onClick={() => {setLocation('');setLocationSource('')}}>Alterar</button></div> : <><button className="location-button" onClick={requestLocation} disabled={locationStatus === 'loading'}><Icon name="pin" size={18}/>{locationStatus === 'loading' ? 'Buscando localização…' : 'Usar localização do dispositivo'}</button>{locationStatus === 'error' && <p className="location-error">Não foi possível acessar a localização. Você pode informar um endereço abaixo ou usar seu endereço cadastrado.</p>}<label className="manual-location">Ou digite um endereço para esta ocorrência<input value={manualAddress} onChange={(e) => setManualAddress(e.target.value)} placeholder="Rua, bairro, cidade"/></label><p className="location-fallback"><Icon name="check" size={15}/> Se preferir, usaremos seu endereço cadastrado: <strong>{profile?.address}</strong></p></>}</div><button className="primary-button wide flow-continue" onClick={continueToReview}>Continuar para revisão <Icon name="arrow" size={19}/></button></div><aside className="flow-aside"><div className="aside-symbol"><Icon name="logo" size={32}/></div><h3>Sua segurança vem primeiro.</h3><p>Se estiver em perigo imediato, afaste-se da área de risco e procure os canais oficiais de emergência.</p><div className="aside-line"/><p className="aside-small">Esta etapa é uma demonstração. Nenhum alerta real será enviado.</p></aside></section>}

      {screen === 'review' && profile && <section className="flow-layout"><div className="flow-main"><div className="flow-progress"><span className="progress-active"/><span className="progress-active"/><span/></div><p className="step-description">Confira as informações. Ao confirmar, o registro será salvo apenas nesta demonstração.</p><div className="review-card"><div className="review-section"><span className="review-label">SITUAÇÃO INFORMADA</span><div className="review-type"><span className="occ-icon occ-0"><Icon name={occurrences.find(x=>x.label===selectedType)?.icon || 'alert'}/></span><strong>{selectedType}</strong></div>{notes && <p className="review-notes">{notes}</p>}</div><div className="review-divider"/><div className="review-section"><span className="review-label">DADOS PARA CONTATO</span><ReviewRow label="Nome" value={profile.name}/><ReviewRow label="Telefone" value={profile.phone}/><ReviewRow label="Endereço cadastrado" value={profile.address}/></div><div className="review-divider"/><div className="review-section"><span className="review-label">LOCAL DA OCORRÊNCIA</span><div className="review-location"><Icon name="pin" size={18}/><span><strong>{location || profile.address}</strong><small>{locationSource || 'Endereço cadastrado'}</small></span></div></div><div className="simulated-callout"><Icon name="alert" size={19}/><p><strong>Envio simulado</strong> — nenhum dado será enviado à Defesa Civil ou a serviços externos.</p></div></div><div className="review-actions"><button className="secondary-button" onClick={() => navigate('choose')}><Icon name="back" size={18}/> Voltar e editar</button><button className="primary-button" onClick={confirmAlert}>Confirmar registro simulado <Icon name="arrow" size={19}/></button></div></div><aside className="flow-aside"><div className="aside-symbol"><Icon name="lock" size={30}/></div><h3>Seus dados continuam com você.</h3><p>Esta informação será guardada localmente neste dispositivo para que você consulte depois.</p><div className="aside-line"/><p className="aside-small">Nenhum órgão público receberá este registro.</p></aside></section>}

      {screen === 'success' && currentAlert && <section className="success-wrap"><div className="success-card"><div className="success-mark"><Icon name="check" size={34}/></div><h2>Pronto, seu alerta foi<br/><span>registrado na demonstração.</span></h2><p className="success-lead">O registro foi salvo neste dispositivo. Este protótipo não enviou informações para nenhum serviço.</p><div className="success-id"><span>IDENTIFICADOR DO REGISTRO</span><strong>{currentAlert.id}</strong><small>{formatDate(currentAlert.date)}</small></div><div className="success-simulation"><Icon name="alert" size={20}/><p><strong>Envio simulado.</strong> A Defesa Civil não recebeu este alerta e nenhum atendimento foi acionado.</p></div><div className="success-actions"><button className="primary-button" onClick={() => navigate('home')}><Icon name="home" size={18}/> Voltar ao início</button><button className="secondary-button" onClick={() => navigate('history')}><Icon name="history" size={18}/> Consultar histórico</button></div><p className="real-emergency">Em uma situação real, procure os canais oficiais de emergência.</p></div></section>}

      {screen === 'history' && <section className="content-narrow"><div className="history-toolbar"><p>{alerts.length ? `${alerts.length} registro${alerts.length > 1 ? 's' : ''} salvo${alerts.length > 1 ? 's' : ''} neste dispositivo` : 'Seus registros de demonstração aparecerão aqui.'}</p><button className="secondary-button" onClick={beginAlert}><Icon name="plus" size={17}/> Novo alerta</button></div>{alerts.length === 0 ? <div className="empty-state"><span className="empty-icon"><Icon name="history" size={31}/></span><h2>Nenhum alerta por aqui</h2><p>Quando você concluir uma demonstração de alerta, ela aparecerá nesta lista.</p><button className="primary-button" onClick={beginAlert}>Registrar um alerta <Icon name="arrow" size={18}/></button></div> : <div className="history-list">{alerts.map((alert) => <button className="history-item" key={alert.id} onClick={() => viewAlert(alert)}><span className="history-type-icon"><Icon name={occurrences.find(x=>x.label===alert.type)?.icon || 'alert'}/></span><span className="history-info"><strong>{alert.type}</strong><small><Icon name="clock" size={14}/>{formatDate(alert.date)}</small><small><Icon name="pin" size={14}/>{alert.location}</small></span><span className="simulated-tag"><i/> Simulado</span><Icon name="arrow" size={18} className="history-arrow"/></button>)}</div>}<DemoNotice compact/></section>}

      {screen === 'detail' && activeAlert && <section className="content-narrow"><div className="detail-card"><div className="detail-top"><span className="occ-icon occ-0"><Icon name={occurrences.find(x=>x.label===activeAlert.type)?.icon || 'alert'} size={25}/></span><div><h2>{activeAlert.type}</h2></div><span className="simulated-tag"><i/> Simulado</span></div><div className="detail-grid"><ReviewRow label="Identificador fictício" value={activeAlert.id}/><ReviewRow label="Data e horário" value={formatDate(activeAlert.date)}/><ReviewRow label="Localização informada" value={activeAlert.location}/><ReviewRow label="Origem da localização" value={activeAlert.source}/><ReviewRow label="Nome" value={activeAlert.profile.name}/><ReviewRow label="Telefone" value={activeAlert.profile.phone}/>{activeAlert.notes && <ReviewRow label="Observação" value={activeAlert.notes}/>}</div><div className="simulated-callout"><Icon name="alert" size={19}/><p><strong>Este registro foi apenas simulado.</strong> Nenhuma informação foi enviada a órgãos públicos ou serviços de emergência.</p></div></div><button className="secondary-button back-history" onClick={() => navigate('history')}><Icon name="back" size={18}/> Voltar ao histórico</button><DemoNotice/></section>}
    </main>
    {showNav && <nav className="mobile-nav" aria-label="Navegação inferior"><button className={screen === 'home' ? 'active' : ''} onClick={() => navigate('home')}><Icon name="home" size={20}/>Início</button><button className={screen === 'history' ? 'active' : ''} onClick={() => navigate('history')}><Icon name="history" size={20}/>Histórico</button><button className={screen === 'profile' ? 'active' : ''} onClick={() => navigate('profile')}><Icon name="user" size={20}/>Perfil</button></nav>}
    <footer className="site-footer"><span>© SOS LOCALIZA · Projeto acadêmico</span><span>Uma demonstração de cuidado e prevenção</span></footer>
  </div>
}

function DemoNotice({ compact = false }: { compact?: boolean }) {
  return <div className={`demo-notice ${compact ? 'compact' : ''}`}><span className="notice-icon"><Icon name="alert" size={18}/></span><p><strong>Este é um protótipo acadêmico.</strong> Não substitui os canais oficiais de emergência e não envia alertas reais. Em uma situação real, procure os canais oficiais.</p></div>
}
function ProfileField({ label, value }: { label: string; value: string }) { return <div className="profile-field"><span>{label}</span><strong>{value}</strong></div> }
function ReviewRow({ label, value }: { label: string; value: string }) { return <div className="review-row"><span>{label}</span><strong>{value}</strong></div> }
