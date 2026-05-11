# Design tokens

## Philosophy

Cursor Agents should feel calm, dense, and terminal-adjacent without becoming austere. The visual system borrows the crisp hierarchy of Linear, the command-center density of Raycast, and the focused surface language of Cursor's dashboard. Material 3 remains underneath so color roles, type scale, touch targets, and accessibility primitives come for free. Everything sits on one 4dp spacing grid. The app supports light and dark themes, with dark treated as the default because this is a developer tool and most developers will live there.

## Color

Primitive contrast is measured against the opposite theme surface used by the token's semantic role: light tokens against dark `surface` (`#0D0E12`) and dark tokens against light `surface` (`#FAFAFB`).

| Token | Hex | Contrast vs opposite surface |
| --- | --- | --- |
| `cursor-black` | `#050507` | 19.52:1 |
| `cursor-white` | `#FFFFFF` | 19.29:1 |
| `cursor-gray-50` | `#FAFAFB` | 18.49:1 |
| `cursor-gray-100` | `#F2F3F5` | 17.37:1 |
| `cursor-gray-200` | `#E4E6EA` | 15.44:1 |
| `cursor-gray-300` | `#CDD0D8` | 12.50:1 |
| `cursor-gray-400` | `#AEB4C0` | 9.27:1 |
| `cursor-gray-500` | `#8E95A3` | 2.89:1 |
| `cursor-gray-600` | `#6F7785` | 4.33:1 |
| `cursor-gray-700` | `#545B68` | 6.55:1 |
| `cursor-gray-800` | `#383E49` | 10.31:1 |
| `cursor-gray-900` | `#1F2430` | 14.88:1 |
| `cursor-gray-950` | `#0D0E12` | 18.49:1 |
| `cursor-accent-50` | `#F1F2FF` | 17.35:1 |
| `cursor-accent-100` | `#E1E4FF` | 15.38:1 |
| `cursor-accent-200` | `#C7CCFF` | 12.41:1 |
| `cursor-accent-300` | `#A8B0FF` | 9.48:1 |
| `cursor-accent-400` | `#8791F2` | 2.73:1 |
| `cursor-accent-500` | `#6F7AE3` | 3.62:1 |
| `cursor-accent-600` | `#5E6AD2` | 4.51:1 |
| `cursor-accent-700` | `#4C56B8` | 6.04:1 |
| `cursor-accent-800` | `#3D4594` | 8.13:1 |
| `cursor-accent-900` | `#30376F` | 10.59:1 |

| Semantic token | Light | Dark |
| --- | --- | --- |
| `surface` | `#FAFAFB` | `#0D0E12` |
| `surfaceVariant` | `#F2F3F5` | `#1F2430` |
| `surfaceContainer` | `#FFFFFF` | `#11131A` |
| `onSurface` | `#0D0E12` | `#FAFAFB` |
| `onSurfaceVariant` | `#545B68` | `#AEB4C0` |
| `primary` | `#5E6AD2` | `#8791F2` |
| `onPrimary` | `#FFFFFF` | `#0D0E12` |
| `primaryContainer` | `#E1E4FF` | `#3D4594` |
| `onPrimaryContainer` | `#30376F` | `#F1F2FF` |
| `outline` | `#8E95A3` | `#6F7785` |
| `outlineVariant` | `#E4E6EA` | `#383E49` |
| `error` | `#D92D20` | `#FF6B66` |
| `onError` | `#FFFFFF` | `#0D0E12` |
| `success` | `#16803C` | `#44C285` |
| `onSuccess` | `#FFFFFF` | `#0D0E12` |
| `warning` | `#A15C00` | `#E0B341` |
| `onWarning` | `#FFFFFF` | `#0D0E12` |

| `AgentStatus` | Light foreground | Light background | Dark foreground | Dark background |
| --- | --- | --- | --- | --- |
| `CREATING` | `#7A4B00` | `#FFF2CC` | `#FFE3A3` | `#3D2E0A` |
| `RUNNING` | `#0B5CAD` | `#DDEEFF` | `#A8D4FF` | `#102A43` |
| `FINISHED` | `#0F6B35` | `#DFF7E9` | `#A7F3C4` | `#10351F` |
| `STOPPED` | `#545B68` | `#E4E6EA` | `#CDD0D8` | `#2A2F3A` |
| `FAILED` | `#B42318` | `#FFE3E0` | `#FFB4AD` | `#3F1516` |

## Type

Use the Material 3 type scale as the base. Inter is the family for display, headline, title, and body roles. JetBrains Mono is used for `labelSmall` when rendering code-like metadata: commit SHAs, agent IDs, repository refs, and status badge labels.

| Token | Font family | Weight | Size (sp) | Line height (sp) | Letter spacing |
| --- | --- | --- | --- | --- | --- |
| `displayLarge` | Inter | Normal | 57 | 64 | -0.25sp |
| `displayMedium` | Inter | Normal | 45 | 52 | 0sp |
| `displaySmall` | Inter | Normal | 36 | 44 | 0sp |
| `headlineLarge` | Inter | Normal | 32 | 40 | 0sp |
| `headlineMedium` | Inter | Normal | 28 | 36 | 0sp |
| `headlineSmall` | Inter | Normal | 24 | 32 | 0sp |
| `titleLarge` | Inter | Normal | 22 | 28 | 0sp |
| `titleMedium` | Inter | Medium | 16 | 24 | 0.15sp |
| `titleSmall` | Inter | Medium | 14 | 20 | 0.1sp |
| `bodyLarge` | Inter | Normal | 16 | 24 | 0.5sp |
| `bodyMedium` | Inter | Normal | 14 | 20 | 0.25sp |
| `bodySmall` | Inter | Normal | 12 | 16 | 0.4sp |
| `labelLarge` | Inter | Medium | 14 | 20 | 0.1sp |
| `labelMedium` | Inter | Medium | 12 | 16 | 0.5sp |
| `labelSmall` | JetBrains Mono | Medium | 11 | 16 | 0.5sp |

## Spacing

The app uses one 4dp spacing scale exposed through `LocalSpacing`.

| Token | Value |
| --- | --- |
| `xxs` | 4dp |
| `xs` | 8dp |
| `s` | 12dp |
| `m` | 16dp |
| `l` | 24dp |
| `xl` | 32dp |
| `xxl` | 48dp |
| `xxxl` | 64dp |

Never use bare `.dp` literals in composables. Always go through `LocalSpacing.current.X` for spacing so density changes stay consistent across screens.

## Radii

| Token | Value | Usage |
| --- | --- | --- |
| `none` | 0dp | Flush edges and dividers |
| `xs` | 4dp | Small controls and text fields |
| `s` | 8dp | Buttons |
| `m` | 12dp | Cards |
| `l` | 16dp | Sheets |
| `xl` | 24dp | Large panels |
| `full` | 9999dp | Status badges and pills |

Compose `Shapes` map these as `extraSmall=4dp`, `small=8dp`, `medium=12dp`, `large=16dp`, and `extraLarge=24dp`.

## Motion

| Token | Value | Use |
| --- | --- | --- |
| `fast` | 120ms | Press, ripple, focus, and small affordance feedback |
| `medium` | 200ms | Screen-level transitions and standard state changes |
| `slow` | 320ms | Content reveal, empty states, and larger entrance/exit motion |

| Token | Easing | Use |
| --- | --- | --- |
| `standard` | `FastOutSlowInEasing` | Default Material movement |
| `emphasized` | `cubic-bezier(0.2, 0, 0, 1)` | More deliberate content reveal |

## Elevation

Use Material 3 `Surface.tonalElevation` rather than bespoke shadows.

| Level | Value | Surface tier |
| --- | --- | --- |
| 0 | 0dp | Base screen `surface` |
| 1 | 1dp | `surfaceContainer` cards and toolbars |
| 2 | 3dp | Raised cards and inline panels |
| 3 | 6dp | Menus, dialogs, and sheets |
| 4 | 8dp | Temporary overlays and blocking surfaces |

## Layout

Edge insets use `screenPadding = LocalSpacing.current.m` (16dp). Tablet and large-screen content should cap at 600dp unless the view is intentionally data-dense. Minimum touch target size is 48dp.

## Status badge spec

Status badges are pills using `radii.full`, 4dp vertical padding, and 8dp horizontal padding. Labels use JetBrains Mono `labelSmall`, uppercase text, and the foreground/background pair from the status color table. `ui/components/StatusBadge.kt` is the reusable component for all status labels.

## Implementation map

| Spec section | Runtime source of truth |
| --- | --- |
| Color | `app/src/main/kotlin/fr/lawmight/cursoragents/ui/theme/Color.kt` |
| Type | `app/src/main/kotlin/fr/lawmight/cursoragents/ui/theme/Type.kt` |
| Spacing | `app/src/main/kotlin/fr/lawmight/cursoragents/ui/theme/Spacing.kt` |
| Radii | `app/src/main/kotlin/fr/lawmight/cursoragents/ui/theme/Shape.kt` |
| Motion | `app/src/main/kotlin/fr/lawmight/cursoragents/ui/theme/Motion.kt` |
| Elevation and layout defaults | `app/src/main/kotlin/fr/lawmight/cursoragents/ui/theme/Theme.kt` |
| Status badges | `app/src/main/kotlin/fr/lawmight/cursoragents/ui/components/StatusBadge.kt` |
