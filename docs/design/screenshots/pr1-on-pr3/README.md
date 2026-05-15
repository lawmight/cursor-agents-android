# PR #1 on PR #3 visual review blocker

This branch layers:

- PR #3 (`cursor/design-tokens-specification-4ab4`)
- PR #6 (`cursor/android-toolchain-1474`)
- PR #1 (`cursor/onboarding-screen-api-key-validation-5800`)

The merged tree builds successfully with:

```bash
./gradlew :app:assembleDebug
```

Screenshot capture is blocked in this cloud VM by emulator host capabilities:

- Pixel 6 API 34 x86_64 AVD cannot start because `/dev/kvm` is unavailable:
  `x86_64 emulation currently requires hardware acceleration`.
- API 34 ARM64 fallback cannot start on this x86_64 host:
  `Avd's CPU Architecture 'arm64' is not supported by the QEMU2 emulator on x86_64 host`.
- `modprobe` is unavailable in the VM, so KVM cannot be enabled here.

The requested screenshots should be captured on a local or CI runner with KVM
available using the debug APK produced by the build above. Target paths:

```text
docs/design/screenshots/pr1-on-pr3/light/idle.png
docs/design/screenshots/pr1-on-pr3/light/typed.png
docs/design/screenshots/pr1-on-pr3/light/reveal-password.png
docs/design/screenshots/pr1-on-pr3/light/validating.png
docs/design/screenshots/pr1-on-pr3/light/validation-failed.png
docs/design/screenshots/pr1-on-pr3/light/connected.png
docs/design/screenshots/pr1-on-pr3/light/cleared.png
docs/design/screenshots/pr1-on-pr3/dark/idle.png
docs/design/screenshots/pr1-on-pr3/dark/typed.png
docs/design/screenshots/pr1-on-pr3/dark/reveal-password.png
docs/design/screenshots/pr1-on-pr3/dark/validating.png
docs/design/screenshots/pr1-on-pr3/dark/validation-failed.png
docs/design/screenshots/pr1-on-pr3/dark/connected.png
docs/design/screenshots/pr1-on-pr3/dark/cleared.png
```

What's worth checking once screenshots are captured:

- Typography contrast
- Error message styling
- Button states
- Splash and onboarding handoff
- Dark mode parity
