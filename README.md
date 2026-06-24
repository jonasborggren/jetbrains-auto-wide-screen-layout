# Auto Widescreen Layout Plugin

An IntelliJ Platform plugin that automatically toggles the built-in **"Widescreen tool window layout"** setting depending on whether the IDE window itself is wide. 

Perfect for ultrawide monitors, multi-monitor setups, or developers who frequently resize their IDE window and want their tool windows (such as the Project view, Terminal, and Run windows) to dynamically rearrange for optimal space utilization.

---

## Features

- 🔄 **Automatic Layout Toggling**: Automatically enables or disables the IDE's widescreen layout when your window crosses a configured threshold.
- 📐 **Dual Trigger Modes**:
  - **Screen Width Mode**: Triggers when the window width in pixels exceeds the threshold (e.g. `1920px`).
  - **Aspect Ratio Mode**: Triggers when the aspect ratio (width / height) exceeds the threshold (e.g. `1.78` for 16:9 widescreen or `2.33` for 21:9 ultrawide).
- 📊 **Live IDE Window Feedback**: Displays your current window dimensions, calculated aspect ratio, and friendly ratio helpers (e.g. *Ultrawide*, *Widescreen*, *Standard*) directly inside the settings page to make configuring your threshold incredibly simple.
- ⚡ **Zero-Overhead & Safe**: Uses native Swing component listeners bound to project lifecycles to ensure zero background overhead and clean memory disposal when windows close.

---

## Configuration

Once installed, navigate to:
**Settings/Preferences** > **Appearance & Behavior** > **Auto Widescreen Layout**

1. **Enable** the checkbox to activate automatic switching.
2. Select your preferred **Trigger Mode** (Width or Aspect Ratio).
3. Use the slider to define your threshold.
4. View your **Current IDE window** size at the bottom to see what value is right for you.

---

## Building from Source

This project uses the modern `intellij-platform-gradle-plugin` (2.x) and targets **JVM 21**.

### Prerequisites
- Java Development Kit (JDK) 21

### Build the Plugin Package
To compile the plugin and generate the `.zip` archive:
```bash
./gradlew buildPlugin
```
The compiled plugin package will be created at:
`build/distributions/jetbrains-auto-wide-screen-layout-1.0.0.zip`

### Run in Sandbox IDE
To test and debug the plugin locally inside a sandboxed IDE instance:
```bash
./gradlew runIde
```

---

## Installation

1. Download the latest release `.zip` file (or build it from source).
2. Open your JetBrains IDE (Android Studio, IntelliJ IDEA, PyCharm, etc.).
3. Navigate to **Settings/Preferences** > **Plugins**.
4. Click the **Gear icon** (Settings) in the top right and select **Install Plugin from Disk...**
5. Select the `.zip` file and restart the IDE.

---

## Publishing

To publish updates to the JetBrains Marketplace, get your marketplace API publishing token and run:
```bash
PUBLISH_TOKEN="your_marketplace_token" ./gradlew publishPlugin
```
