import { useEffect, useRef } from 'react'
import Icon from './Icon'
import { occurrences, type Occurrence } from './occurrences'
import { safetyGuides, safetySources, type SafetyGuide, type SafetyPhase, type SafetySourceId } from './safetyContent'
import './SafetyGuidance.css'

const phases: { id: SafetyPhase; title: string; icon: string; expandable: boolean }[] = [
  { id: 'before', title: 'Como se prevenir', icon: 'logo', expandable: true },
  { id: 'during', title: 'O que fazer durante a ocorrência', icon: 'alert', expandable: false },
  { id: 'after', title: 'Depois que o perigo passar', icon: 'checkCircle', expandable: true },
  { id: 'help', title: 'Quando buscar ajuda', icon: 'help', expandable: false },
]

type Props = {
  selected: Occurrence | null
  onSelect: (occurrence: Occurrence | null) => void
  onHome: () => void
}

function SourceLinks({ ids }: { ids: readonly SafetySourceId[] }) {
  return <p className="safety-section-sources">Fontes: {ids.map((id, index) => <span key={id}>
    {index > 0 && ' · '}<a href={safetySources[id].url} target="_blank" rel="noopener noreferrer">{safetySources[id].organization}<span className="sr-only"> — {safetySources[id].title} (abre em nova aba)</span></a>
  </span>)}</p>
}

export default function SafetyGuidance({ selected, onSelect, onHome }: Props) {
  const headingRef = useRef<HTMLHeadingElement>(null)
  const keepSelectFocus = useRef(false)
  const guide: SafetyGuide | null = selected ? safetyGuides[selected] : null
  const sourceIds = guide ? [...new Set(phases.flatMap(({ id }) => [...guide.sections[id].sources]))] : []

  useEffect(() => {
    if (!keepSelectFocus.current) {
      window.scrollTo(0, 0)
      headingRef.current?.focus({ preventScroll: true })
    }
    keepSelectFocus.current = false
  }, [selected])

  return <section className="content-narrow safety-content" aria-label="Consulta de orientações de segurança">
    <div className="safety-emergency"><Icon name="alert" size={20}/><p><strong>Perigo imediato?</strong> Procure os serviços oficiais. Não aguarde um registro neste aplicativo para buscar ajuda. <a href={safetySources.civilDefense.url} target="_blank" rel="noopener noreferrer">Orientação da Defesa Civil<span className="sr-only"> (abre em nova aba)</span></a>.</p></div>

    {!guide ? <>
      <h2 ref={headingRef} tabIndex={-1}>Qual situação você quer consultar?</h2>
      <p className="safety-intro">Escolha uma situação para ver como se prevenir, agir e buscar ajuda.</p>
      <div className="safety-topics">
        {occurrences.map((occurrence) => <button key={occurrence.label} className="occurrence-option safety-topic" onClick={() => onSelect(occurrence.label)} aria-label={`Ver orientações: ${safetyGuides[occurrence.label].title}`}>
          <Icon name={occurrence.icon} size={24}/><span className="occ-copy"><strong>{safetyGuides[occurrence.label].title}</strong><small>{occurrence.hint}</small></span><Icon name="arrow" size={20}/>
        </button>)}
      </div>
    </> : <>
      <div className="safety-topic-toolbar">
        <button className="back-button" onClick={() => onSelect(null)}><Icon name="back" size={18}/> Ver todas as situações</button>
        <label className="safety-select">Situação<select value={selected || ''} onChange={(event) => {
          keepSelectFocus.current = true
          onSelect(event.target.value as Occurrence)
        }}>{occurrences.map(({ label }) => <option key={label} value={label}>{safetyGuides[label].shortTitle}</option>)}</select></label>
      </div>
      <article className="safety-guide" aria-labelledby="safety-guide-title">
        <h2 id="safety-guide-title" ref={headingRef} tabIndex={-1}>{guide.title}</h2>
        {guide.general && <p className="safety-intro">Orientações gerais. Situações diferentes podem exigir medidas específicas: siga as autoridades locais.</p>}
        <div className="safety-sections" key={selected}>
          {phases.map((phase) => {
            const content = guide.sections[phase.id]
            const body = <><ul>{content.items.map((item) => <li key={item}>{item}</li>)}</ul><SourceLinks ids={content.sources}/></>
            return phase.expandable
              ? <details className="safety-phase" key={phase.id}><summary><Icon name={phase.icon} size={21}/><span>{phase.title}</span><Icon name="chevron" size={18} className="safety-chevron"/></summary>{body}</details>
              : <section className="safety-phase safety-phase-open" key={phase.id} aria-labelledby={`safety-${phase.id}`}><h3 id={`safety-${phase.id}`}><Icon name={phase.icon} size={21}/>{phase.title}</h3>{body}</section>
          })}
        </div>
        <section className="safety-references" aria-labelledby="safety-sources-title">
          <h3 id="safety-sources-title">Fontes oficiais consultadas</h3>
          <ul>{sourceIds.map((id) => <li key={id}><a href={safetySources[id].url} target="_blank" rel="noopener noreferrer">{safetySources[id].title}<span className="sr-only"> (abre em nova aba)</span></a><span>{safetySources[id].organization} · Consulta em <time dateTime={safetySources[id].checkedAt}>{safetySources[id].checkedAt.split('-').reverse().join('/')}</time></span></li>)}</ul>
        </section>
      </article>
    </>}

    <p className="safety-authorities">Estas orientações não substituem as instruções das autoridades locais. Uma estimativa meteorológica não confirma que um desastre acontecerá.</p>
    <button className="secondary-button safety-home" onClick={onHome}><Icon name="home" size={18}/> Voltar ao início</button>
  </section>
}
