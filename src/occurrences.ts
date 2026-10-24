export type Occurrence = 'Alagamento' | 'Enchente' | 'Desmoronamento ou barranco em risco' | 'Casa com risco de desabamento' | 'Outra situação'

export const occurrences: { label: Occurrence; icon: string; hint: string }[] = [
  { label: 'Alagamento', icon: 'water', hint: 'Água acumulada em ruas ou áreas próximas' },
  { label: 'Enchente', icon: 'waves', hint: 'Elevação ou transbordamento de água' },
  { label: 'Desmoronamento ou barranco em risco', icon: 'mountain', hint: 'Terra cedendo ou risco de deslizamento' },
  { label: 'Casa com risco de desabamento', icon: 'house', hint: 'Danos estruturais ou risco na moradia' },
  { label: 'Outra situação', icon: 'plus', hint: 'Descreva uma situação diferente' },
]
