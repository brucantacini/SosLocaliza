import type { Occurrence } from './occurrences'

export const safetySources = {
  flooding: {
    organization: 'Defesa Civil Nacional',
    title: 'Medidas de autoproteção em alagamentos',
    url: 'https://www.gov.br/mdr/pt-br/ultimas-noticias/conheca-medidas-de-autoprotecao-que-a-populacao-deve-tomar-em-casos-de-alagamento',
    checkedAt: '2026-10-23',
  },
  landslide: {
    organization: 'Defesa Civil Nacional',
    title: 'Medidas de autoproteção em deslizamentos',
    url: 'https://www.gov.br/mdr/pt-br/ultimas-noticias/conheca-medidas-de-autoprotecao-que-a-populacao-deve-tomar-em-casos-de-deslizamentos',
    checkedAt: '2026-10-23',
  },
  inundation: {
    organization: 'Corpo de Bombeiros Militar de Santa Catarina',
    title: 'Inundações: antes, durante e depois',
    url: 'https://www.cbm.sc.gov.br/index.php/dicas-de-prevencao/inundacoes',
    checkedAt: '2026-10-23',
  },
  civilDefense: {
    organization: 'Defesa Civil de Criciúma',
    title: 'Perguntas frequentes: sinais de risco e busca de ajuda',
    url: 'https://www.defesacivil.criciuma.sc.gov.br/faq',
    checkedAt: '2026-10-23',
  },
  rescue: {
    organization: 'Corpo de Bombeiros Militar de Minas Gerais',
    title: 'Como solicitar atendimento de urgência e emergência',
    url: 'https://www.mg.gov.br/servico/solicitar-atendimento-de-urgencia-e-emergencia-pelo-corpo-de-bombeiros-militar?page=1%2C0',
    checkedAt: '2026-10-23',
  },
} as const

export type SafetySourceId = keyof typeof safetySources
export type SafetyPhase = 'before' | 'during' | 'after' | 'help'
type SafetySection = { items: readonly string[]; sources: readonly SafetySourceId[] }
export type SafetyGuide = {
  title: string
  shortTitle: string
  general?: boolean
  sections: Record<SafetyPhase, SafetySection>
}

const emergencyHelp: SafetySection = {
  items: [
    'Em situação de risco, acione a Defesa Civil pelo 199. Para resgate ou perigo imediato à vida, ligue para os Bombeiros: 193.',
    'Descreva o que aconteceu e informe o endereço e um ponto de referência. Siga as instruções do atendimento.',
  ],
  sources: ['inundation', 'rescue'],
}

// Keys remain identical to the occurrence values already saved in localStorage.
export const safetyGuides = {
  Alagamento: {
    title: 'Alagamento',
    shortTitle: 'Alagamento',
    sections: {
      before: {
        items: ['Antes das chuvas, mantenha ralos e calhas limpos.', 'Descarte o lixo corretamente e avise a prefeitura sobre bueiros obstruídos.'],
        sources: ['flooding'],
      },
      during: {
        items: ['Não atravesse ruas ou pontes alagadas, a pé ou em veículos.', 'Evite contato com a água acumulada. Se precisar sair, busque um abrigo seguro.'],
        sources: ['flooding'],
      },
      after: {
        items: ['Continue evitando a água que restou: ela pode estar contaminada.', 'Aguarde a orientação das equipes de socorro antes de reocupar o local atingido.'],
        sources: ['flooding', 'inundation'],
      },
      help: emergencyHelp,
    },
  },
  Enchente: {
    title: 'Enchente',
    shortTitle: 'Enchente',
    sections: {
      before: {
        items: ['Combine com a família uma rota fora das áreas inundáveis e um lugar seguro para ficar.', 'Deixe água potável, documentos e medicamentos necessários em um kit acessível.'],
        sources: ['inundation'],
      },
      during: {
        items: ['Siga as ordens de evacuação e saia enquanto o caminho ainda for seguro.', 'Não caminhe nem dirija por áreas inundadas. Procure um ponto elevado, fora da água.'],
        sources: ['inundation'],
      },
      after: {
        items: ['Espere a liberação das equipes responsáveis para voltar ao imóvel.', 'Mantenha distância da água e das áreas afetadas; acompanhe os comunicados oficiais.'],
        sources: ['inundation'],
      },
      help: emergencyHelp,
    },
  },
  'Desmoronamento ou barranco em risco': {
    title: 'Desmoronamento ou deslizamento de terra',
    shortTitle: 'Deslizamento de terra',
    sections: {
      before: {
        items: ['Preserve a vegetação das encostas.', 'Não despeje lixo, entulho, água ou esgoto sobre o terreno.'],
        sources: ['landslide'],
      },
      during: {
        items: ['Rachaduras no chão, postes inclinados ou movimentação do terreno exigem atenção: saia da área e avise as autoridades.', 'Não permaneça junto de encostas, muros ou estruturas atingidas.'],
        sources: ['landslide', 'civilDefense'],
      },
      after: {
        items: ['Não entre na área atingida sem autorização.', 'Só volte para casa após a liberação da Defesa Civil ou do órgão responsável.'],
        sources: ['landslide'],
      },
      help: emergencyHelp,
    },
  },
  'Casa com risco de desabamento': {
    title: 'Risco de desabamento ou casa cedendo',
    shortTitle: 'Risco de desabamento',
    sections: {
      before: {
        items: ['Solicite avaliação se aparecerem rachaduras repentinas, que aumentam rapidamente, ou inclinação de paredes e pisos.'],
        sources: ['civilDefense'],
      },
      during: {
        items: ['Se houver risco aparente, saia e mantenha distância da estrutura.', 'Avise outras pessoas e não tente fazer reparos por conta própria.'],
        sources: ['civilDefense'],
      },
      after: {
        items: ['Não volte a ocupar uma estrutura com risco. Solicite avaliação e siga a orientação da Defesa Civil.'],
        sources: ['civilDefense'],
      },
      help: {
        items: ['Acione a Defesa Civil: 199. Se houver pessoas feridas ou presas, chame também os Bombeiros: 193.', 'Informe o endereço e descreva os sinais observados.'],
        sources: ['civilDefense', 'rescue'],
      },
    },
  },
  'Outra situação': {
    title: 'Outras situações de emergência',
    shortTitle: 'Outras emergências',
    general: true,
    sections: {
      before: {
        items: ['Combine como falar com familiares e onde se encontrar em uma emergência.', 'Conheça os canais oficiais e mantenha o celular carregado.'],
        sources: ['inundation'],
      },
      during: {
        items: ['Siga as instruções das autoridades e evite deslocamentos para áreas de risco.', 'Em uma situação urgente, não espere a análise de um formulário para buscar socorro.'],
        sources: ['civilDefense'],
      },
      after: {
        items: ['Acompanhe as orientações oficiais e aguarde a liberação do local afetado.', 'Avise familiares ou pessoas de confiança que você está em segurança.'],
        sources: ['inundation'],
      },
      help: emergencyHelp,
    },
  },
} satisfies Record<Occurrence, SafetyGuide>
