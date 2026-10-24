---
name: SOS LOCALIZA
description: A light, approachable interface for nearby weather and simulated occurrence records.
colors:
  background: "#f1f0ed"
  surface: "#fff"
  surface-soft: "#f8f7f5"
  ink: "#123743"
  muted: "#496875"
  line: "#d8dedf"
  accent: "#c9eb9d"
  accent-hover: "#b9df87"
  on-accent: "#123743"
  on-alert: "#ffffff"
  location: "#2f80ed"
  risk-high: "#d64550"
  alert-action: "#cf3c48"
  alert-action-hover: "#bf3440"
  risk-moderate: "#e9a820"
  risk-low: "#258a62"
  danger-text: "#a22535"
  danger-surface: "#fff0f1"
  warning-text: "#705318"
  warning-surface: "#faf3e1"
  control-border: "#789098"
  secondary-hover: "#e5e8e2"
  placeholder: "#60747d"
  selected-surface: "#e7f1d9"
typography:
  headline:
    fontFamily: "Montserrat, Arial, sans-serif"
    fontSize: "36px"
    fontWeight: 700
    lineHeight: 1.22
    letterSpacing: "-.025em"
  headline-mobile:
    fontFamily: "Montserrat, Arial, sans-serif"
    fontSize: "28px"
    fontWeight: 700
    lineHeight: 1.22
    letterSpacing: "-.025em"
  title:
    fontFamily: "Montserrat, Arial, sans-serif"
    fontSize: "24px"
    fontWeight: 700
    lineHeight: 1.3
    letterSpacing: "-.02em"
  body:
    fontFamily: "Montserrat, Arial, sans-serif"
    fontSize: "16px"
    fontWeight: 400
    lineHeight: 1.65
  button:
    fontFamily: "Montserrat, Arial, sans-serif"
    fontSize: "16px"
    fontWeight: 600
    lineHeight: 1.4
  label:
    fontFamily: "Montserrat, Arial, sans-serif"
    fontSize: "12px"
    fontWeight: 600
    lineHeight: 1.5
    letterSpacing: ".025em"
rounded:
  surface: "24px"
  control: "12px"
  pill: "100px"
  map: "20px"
  map-mobile: "16px"
spacing:
  space-1: "8px"
  space-2: "16px"
  space-3: "24px"
  space-4: "32px"
  space-5: "48px"
  space-6: "64px"
components:
  button-primary:
    backgroundColor: "{colors.accent}"
    textColor: "{colors.ink}"
    typography: "{typography.button}"
    rounded: "{rounded.pill}"
    padding: "12px 26px"
  button-primary-hover:
    backgroundColor: "{colors.accent-hover}"
  button-alert:
    backgroundColor: "{colors.alert-action}"
    textColor: "{colors.surface}"
    rounded: "{rounded.pill}"
    padding: "14px 24px"
  button-alert-hover:
    backgroundColor: "{colors.alert-action-hover}"
  button-secondary:
    backgroundColor: "transparent"
    textColor: "{colors.ink}"
    rounded: "{rounded.pill}"
    padding: "12px 26px"
  button-secondary-hover:
    backgroundColor: "{colors.secondary-hover}"
  input:
    backgroundColor: "{colors.surface}"
    textColor: "{colors.ink}"
    rounded: "{rounded.control}"
    padding: "12px 14px"
  form-card:
    backgroundColor: "{colors.surface}"
    rounded: "{rounded.surface}"
    padding: "36px"
  map-section:
    backgroundColor: "{colors.surface}"
    textColor: "{colors.ink}"
    rounded: "{rounded.surface}"
    padding: "24px"
---

# Design System: SOS LOCALIZA

## Overview

The approved visual direction adapts the supplied Webnode references into a light interface: warm gray surroundings, white surfaces, petroleum text, lime ordinary actions and a red action for communicating an occurrence. Broad rounded containers and pill controls make the experience approachable while keeping information readable on phones.

This document captures the implemented React interface in `src/styles.css`, `src/MapSection.css` and `src/App.tsx`. The visual authority is the user-approved reference direction; measurements below describe the current source. No additional metaphor or qualitative brand promise is assumed.

**Key Characteristics:**

- Warm neutral canvas with clear dark text.
- One self-hosted font family throughout.
- Large rounded surfaces and comfortable action targets.
- Color accompanied by readable status labels.
- Visible academic and simulation context.

## Colors

### Primary

Lime is the ordinary action fill; petroleum supplies its text, headings, outlines and active navigation. The frontmatter is the normative palette and preserves source values.

### Themes

The frontmatter above defines the original light theme. `:root[data-theme="dark"]` replaces surface and content roles with the following palette; typography, geometry, brand and semantic colors remain shared. The sidecar's `extensions.themes` records both theme roles.

| Role | Light | Dark |
| --- | --- | --- |
| Background | `#F1F0ED` | `#102A43` |
| Surface | `#FFFFFF` | `#183B56` |
| Raised/soft surface | `#F8F7F5` | `#204763` |
| Main text | `#123743` | `#F1F5F4` |
| Secondary text | `#496875` | `#C1CFD5` |
| Divider | `#D8DEDF` | `#3D5D74` |
| Control border | `#789098` | `#8CA4B3` |
| Placeholder | `#60747D` | `#B0C1CD` |
| Hover surface | `#E5E8E2` | `#294D65` |
| Selected occurrence | `#E7F1D9` | `#294C42` |
| Warning background / text | `#FAF3E1` / `#705318` | `#3D3827` / `#F4D58D` |
| Error background / text | `#FFF0F1` / `#A22535` | `#472C35` / `#FFBBC3` |
| Focus | `#123743` | `#C9EB9D` |

Lime surfaces always use `--on-accent: #123743`, and red actions use white `--on-alert`. Do not substitute the theme's general text color on these fills. The petroleum sidebar retains its white text in both themes. Map tiles are neither inverted nor replaced; marker colors and white halos stay invariant. Controls, attribution, status overlay and legend adopt the theme. Map keyboard focus uses a dark ring and white halo to stay visible on terrain.

The persistent header selector uses sun/moon icons, Claro/Escuro labels, `aria-pressed` and 44px minimum targets. It occupies a second header row at 650px and below. The initial HTML selects stored `sos-localiza-theme` (light/dark), or the device preference, before React renders. Without a manual preference, device changes are followed live. Manual choice wins, synchronizes across tabs, and remains session-only if storage is unavailable. Themes must never remount the map, reset forms or navigate away.

### Secondary

The communicate-alert action uses `alert-action` with white copy. Its darker hover is distinct from `risk-high`, which denotes high meteorological attention. Blue marks map location. Amber and green identify moderate and low attention. These semantic colors do not replace the accompanying words.

### Neutral

The warm background surrounds white panels. Off-white fills grouped content; muted petroleum blue supports descriptions and metadata. Light dividers separate sections, while the stronger control border defines inputs and outlined actions. Dedicated danger and warning text/surface pairs support feedback.

**Semantic color rule.** Keep the red action token distinct from the high attention token. Retain blue location markers and the three attention colors when adding related map UI.

## Typography

Montserrat is self-hosted at `/fonts/montserrat-latin.woff2`, declared as a variable font with weights (100–900), normal style and swap loading. Arial and sans-serif are the fallbacks. Display and body share the same family; no additional font pairing is established.

The headline roles describe the default h1 and its phone size. Home headings have an intermediate size (32px) at the tablet breakpoint. Default h2 uses the title role and becomes (22px) on phones; section overrides include recent records (20px), map headings (24px desktop, 22px phone) and the success heading (24px phone). h3 is (20px/1.35). Body paragraphs use the body role; supporting content is commonly (14px). Preserve these distinctions instead of applying the h1 size to every section heading.

Headings balance their lines and permit long words to wrap. Button labels use semibold weight. The small label role identifies functional fields inside review sections; do not add decorative labels above page titles. Weather values use tabular numerals.

## Layout

The main container is centered with a maximum width (1280px) and desktop outer allowance (64px total). Narrow content and flow screens use a maximum width (960px); registration uses (1120px). The declared rhythm is the six spacing steps in the frontmatter, with component-specific values retained in source.

Home uses a two-to-one map/recent-records grid with a gap (32px). The introduction places copy beside the alert action (370px column). At (1100px) the gap contracts and the action column becomes (330px). At (899px) home, registration and alert-flow grids stack. These are surface compositions, not required layouts for every future screen.

At (650px), the main container has side gutters (16px), h1 becomes the mobile headline role, form padding becomes (24px 20px), and review/success actions stack. Profile and record-detail headers use a two-column icon/text grid with their action or simulation tag on the following row, preserving title width. Desktop navigation gives way to a fixed white bottom bar with safe-area padding. Content and footer reserve space for it when present.

The map frame is (460px) high on desktop, (370px) at or below (650px), with its smaller radius on phones. At (390px), weather factors switch from three columns to stacked rows. Later map overrides remain authoritative over the earlier map height and padding declarations.

## Elevation & Depth

Most application panels are flat white or off-white against the warm canvas. Dividers, fill changes and spacing establish hierarchy. A subtle shared shadow (`0 8px 28px #1237430d`) lifts Leaflet zoom and recenter controls. Map overlays and location markers retain stronger local shadows; these are map visibility treatments, not the standard card style. Exact shadow extensions live in the sidecar.

## Shapes

Major panels use the surface radius. Inputs and local panels use the control radius; action buttons use the pill radius. Map clipping uses its dedicated desktop/mobile radii. Avatars, radio indicators and map markers are circular. Preserve thin control strokes and the selected occurrence's stronger border rather than introducing heavy card outlines across the interface.

## Components

### Buttons

Ordinary actions use lime with petroleum text, a minimum height (48px), semibold type and pill corners. Secondary actions use transparent fill and a stronger outline. The communicate-alert action has an icon/copy/arrow grid, white text, a two-line label and minimum height (72px desktop, 76px phone). Its phone padding is (14px 20px).

Primary and secondary controls transition background and border over (.16s ease). Keyboard focus uses a petroleum outline (3px) offset (4px). Disabled buttons use wait cursors and reduced opacity (.65); map refresh specifically uses (.7). Reduced-motion preference removes transitions and animations.

### Cards / Containers

Registration, profile, review, success and detail surfaces use broad rounded white panels. Registration padding is (36px desktop); mobile reduces it as described in Layout. Recent records use open rows and dividers rather than a stack of elevated cards. The map section is a white rounded container with no outer border after cascade resolution.

### Inputs / Fields

Inputs use white fill, a strong thin border, control corners, minimum height (50px) and padding (12px 14px). Text is (16px) with a line height (1.5). Placeholders use the dedicated muted token. Labels remain visible above inputs, and textarea resizing is vertical. Errors use danger text with readable explanatory copy.

### Selection and status

Occurrence options show an icon, title, description and circular indicator. Selected options combine a pale green surface, petroleum border and filled indicator. Record statuses use ‘Registro local’. Avoid repeated academic/simulation notices. The success heading ‘Alerta registrado no SOS Localiza.’ is immediately followed by ‘A equipe da Defesa Civil não foi notificada por este aplicativo.’ in ordinary, legible 16px body text, without a warning box. Location provenance and weather limitations remain explicit.

### Navigation

Desktop navigation is centered, text-led and underlines the active destination with petroleum. Mobile navigation places icons above labels, with dark semibold active text and muted inactive text. The product mark uses the existing outlined location/shield artwork and SOS LOCALIZA name.

### Weather map

Keep map controls legible on tiles, preserve attribution, and show location provenance, query time, attention labels, weather factors and source/method context around the frame. Blue location markers and colored attention dots are paired with text. Refresh uses the ordinary lime pill treatment. The map's own controls retain compact squared shapes rather than inheriting the action button silhouette.

## Do's and Don'ts

- **Do** reuse the source tokens and the Montserrat/Arial stack.
- **Do** keep the supplied reference world's warm light canvas, petroleum text, lime ordinary actions and red communicate-alert action.
- **Do** preserve the phone heading sizes, stacked layouts, visible field labels and keyboard focus treatment.
- **Do** retain text alongside semantic color and keep simulation/provenance context readable.
- **Don't** treat meteorological attention colors as interchangeable decorative accents.
- **Don't** introduce official agency branding or visual claims that a simulated record triggered a response.
- **Don't** turn the map's stronger marker/overlay shadows into the default treatment for application cards.
