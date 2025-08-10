# Spy Game - Android Mobile Application

A fun and interactive spy game for Android devices where players discover their roles (spy or regular player) and try to identify the spies within a time limit.

## Features

- **Customizable Settings**: Configure number of players (3-unlimited), number of spies, and game duration (5-60 minutes)
- **Dynamic Role Assignment**: Random spy selection with secure role revelation
- **Word List Management**: Add custom words to the game vocabulary
- **Timer-based Gameplay**: Countdown timer with visual and audio alerts
- **Dark Theme**: Modern dark UI with dimmed background imagery
- **Test Functionality**: Test alarm system without waiting full game duration
- **Multi-language Ready**: Easy localization support

## Game Rules

### Setup Phase
1. **Player Configuration**: Set total number of players (minimum 3)
2. **Spy Count**: Configure number of spies (1 to min(floor(players/3), players-2))
3. **Duration**: Set game time (5-60 minutes)
4. **Word Management**: Add custom words to the word list

### Game Phase
1. **Word Selection**: App randomly selects a word from the word list
2. **Spy Assignment**: App randomly assigns spy roles to players
3. **Role Revelation**: Players view their roles one by one:
   - **Spies**: See "You are a SPY in this game!"
   - **Regular Players**: See the selected word
4. **Gameplay**: Timer counts down while players interact
5. **Game End**: Alarm triggers with red flashing screen when time expires

## Installation & Setup

### Prerequisites
- Android Studio Arctic Fox or newer
- Android SDK API level 24 or higher
- Android device or emulator with API 24+

### Building the Project

#### Option 1: Using Android Studio (Recommended)
1. **Open in Android Studio:**
   - Launch Android Studio
   - Open the project directory (`spy_game`)
   - Wait for Gradle sync to complete
   - Click "Build" → "Build Bundle(s) / APK(s)" → "Build APK(s)"

2. **Install on device/emulator:**
   - Connect your Android device or start an emulator
   - Click "Run" button (green triangle) in Android Studio
   - Select your target device

#### Option 2: Command Line (Advanced)
If you have Gradle installed globally:
```bash
gradle assembleDebug
```

If you have the Android SDK and Gradle wrapper properly configured:
```bash
# On Windows
gradlew.bat assembleDebug

# On Mac/Linux
./gradlew assembleDebug
```

**Note:** The project includes gradle wrapper configuration files, but you may need to download the gradle-wrapper.jar file manually or use Android Studio for the initial setup.

## Testing Instructions

### Testing with Android Emulator

#### Prerequisites
- Android Studio installed with Android SDK
- At least 8GB of available disk space for emulator
- Hardware acceleration enabled (Intel HAXM or Hyper-V on Windows)
- **No physical device required** - the emulator creates a virtual Android device on your computer

#### Step-by-Step Setup

1. **Create an Android Virtual Device (AVD):**
   - Open Android Studio
   - Go to **Tools** → **AVD Manager** (or click the device icon in the toolbar)
   - Click **"Create Virtual Device"**
   - **Select Hardware Profile:**
     - Choose a device profile (recommended: **Pixel 4**, **Pixel 5**, or **Pixel 6**)
     - For testing different screen sizes, try **Nexus 7** (tablet) or **Pixel C** (large tablet)
   - **Choose System Image:**
     - Select **API Level 24** or higher (minimum supported)
     - Recommended: **API 30** (Android 11) or **API 33** (Android 13) for best compatibility
     - Choose **x86_64** architecture for better performance
     - Download system image if not already available
   - **Configure AVD:**
     - Give it a descriptive name (e.g., "Spy Game Test - Pixel 4 API 30")
     - **Advanced Settings** (optional but recommended):
       - **RAM**: 2048 MB or higher
       - **VM Heap**: 512 MB
       - **Internal Storage**: 2048 MB
       - **SD Card**: 512 MB (for testing file operations)
   - Click **"Finish"** to create the AVD

2. **Launch and Configure Emulator:**
   - In AVD Manager, click the **play button (▶)** next to your created AVD
   - Wait for the emulator to boot completely (first boot may take 2-3 minutes)
   - **Note**: The emulator creates a virtual Android device window on your computer - no physical device or USB connection needed
   - **Optional Configuration within the emulated device:**
     - Enable **Developer Options**: Settings → About → Tap "Build number" 7 times
     - Enable **USB Debugging**: Settings → Developer Options → USB Debugging (this is virtual debugging within the emulator)
     - Disable animations for faster testing: Settings → Developer Options → Animation scales (set all to 0.5x or off)

3. **Deploy and Run the Spy Game App:**
   - In Android Studio, open the Spy Game project
   - Wait for **Gradle sync** to complete
   - Click the **"Run" button (▶)** in the toolbar or press **Shift+F10**
   - **Select Deployment Target:**
     - Choose your running emulator from the device list (it will appear as a virtual device, not a physical one)
     - If emulator doesn't appear, ensure it's fully booted and try clicking "Refresh"
     - **No USB connection required** - Android Studio communicates with the emulator virtually
   - The app will build and install automatically on the virtual device (first install may take 1-2 minutes)

#### Quick Start: Using an Existing Virtual Device

If you already have an Android Virtual Device (AVD) created:

1. **Start your existing emulator:**
   - Open Android Studio
   - Go to **Tools** → **AVD Manager**
   - Find your existing virtual device and click the **play button (▶)**
   - Wait for the emulator to fully boot (you'll see the Android home screen)

2. **Load the Spy Game:**
   - In Android Studio, open the Spy Game project (`c:\spy_game`)
   - Ensure Gradle sync is complete (check bottom status bar)
   - Click the **"Run" button (▶)** in the toolbar or press **Shift+F10**
   - Select your running virtual device from the deployment target dropdown
   - The app will build and automatically install on your virtual device

3. **Launch the game:**
   - Once installation completes, the Spy Game app will automatically launch
   - You can also find it in the app drawer on your virtual device
   - The app icon will appear on the virtual device's home screen for future access

**Note**: If your virtual device doesn't appear in the deployment targets, make sure it's fully booted and click the refresh button in the device selection dropdown.

#### Emulator Testing Best Practices

4. **Performance Optimization:**
   - **Close other applications** to free up system resources
   - **Enable hardware acceleration**: Ensure Intel HAXM or Hyper-V is enabled
   - **Use Quick Boot**: Enable in AVD settings for faster subsequent starts
   - **Wipe data** if emulator becomes slow: AVD Manager → Actions → Wipe Data

5. **Testing Different Scenarios:**
   - **Rotation Testing**: Press **Ctrl+F11** or **Cmd+R** to rotate device
   - **Back Button**: Use hardware back button or **Alt+Backspace**
   - **Home Button**: Click the home button or press **Alt+Escape**
   - **App Switching**: Press **Alt+Tab** to simulate recent apps
   - **Volume Controls**: Use volume buttons on emulator or **Page Up/Page Down**

6. **Debugging and Logs:**
   - Open **Logcat** in Android Studio: View → Tool Windows → Logcat
   - Filter by package name: `com.spygame`
   - Look for error messages or debug output during testing

#### Troubleshooting Emulator Issues

- **Emulator won't start**: Check if hardware acceleration is enabled
- **App installation fails**: Wipe emulator data and try again
- **Slow performance**: Allocate more RAM to emulator or close other apps
- **Touch not working**: Ensure emulator window has focus
- **App crashes**: Check Logcat for error messages and stack traces

### Testing on Physical Device

1. **Enable Developer Options:**
   - Go to Settings → About Phone
   - Tap "Build Number" 7 times
   - Go back to Settings → Developer Options
   - Enable "USB Debugging"

2. **Connect device and install:**
   - Connect device via USB
   - Run `./gradlew installDebug`
   - Or use Android Studio's "Run" button

### Functional Testing Guide

#### 1. Settings Testing
- **Player Count**: 
  - Try setting less than 3 players (should show error)
  - Test with various player counts (3, 5, 10, etc.)
- **Spy Count**:
  - Verify spy count validation works correctly
  - Test boundary conditions (1 spy, max allowed spies)
- **Duration**:
  - Try setting less than 5 minutes (should show error)
  - Try setting more than 60 minutes (should show error)
- **Word Management**:
  - Add new words to the list
  - Try adding duplicate words (should show warning)
  - Try adding empty words (should show error)

#### 2. Game Flow Testing
- **Role Revelation**:
  - Start a game and verify each player sees appropriate role/word
  - Check that buttons become inactive after use
  - Verify auto-close timer works (5 seconds)
- **Timer Functionality**:
  - Start game timer and verify countdown works
  - Test with short duration for quick testing
  - Verify timer color changes in final minute

#### 3. Alarm Testing
- **Test Alarm Feature**:
  - Use "Test Alarm" button in settings
  - Verify 5-second vibration occurs
  - Check red screen flashing effect
- **Game End Alarm**:
  - Let timer run to completion
  - Verify alarm triggers automatically
  - Check game over screen appears after alarm

#### 4. UI/UX Testing
- **Background Image**:
  - Verify icon.png appears as dimmed background
  - Check readability of text over background
- **Dark Theme**:
  - Verify all screens use consistent dark theme
  - Check color contrast and readability
- **Navigation**:
  - Test all button interactions
  - Verify proper navigation between screens

### Quick Test Mode

For rapid testing without waiting full game duration:
1. Set game duration to 5 minutes (minimum)
2. Use "Test Alarm" button in settings to verify alarm functionality
3. Alternatively, modify the timer in code temporarily for faster testing

## Project Structure

```
spy_game/
├── app/
│   ├── src/main/
│   │   ├── java/com/spygame/
│   │   │   ├── MainActivity.kt          # Main menu screen
│   │   │   ├── SettingsActivity.kt      # Game configuration
│   │   │   └── GameActivity.kt          # Game execution
│   │   ├── res/
│   │   │   ├── layout/                  # UI layouts
│   │   │   ├── values/                  # Strings, colors, themes
│   │   │   ├── drawable/                # Images and icons
│   │   │   └── raw/                     # Word list CSV
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
├── settings.gradle
├── word_list.csv                        # External word list
├── icon.png                            # App icon source
└── README.md
```

## Technical Details

### Architecture
- **MVVM Pattern**: Clean separation of concerns
- **Data Persistence**: SharedPreferences for settings and custom words
- **Resource Management**: Proper handling of CSV files and images

### Key Components
- **MainActivity**: Entry point with navigation
- **SettingsActivity**: Configuration management with validation
- **GameActivity**: Core game logic and timer implementation
- **Role Dialog**: Secure role revelation system

### Dependencies
- AndroidX libraries for modern Android development
- Material Design components for consistent UI
- View Binding for type-safe view references

## Customization

### Adding New Languages
1. Create new `values-<language>/strings.xml` files
2. Translate all string resources
3. Update word list with appropriate vocabulary

### Modifying Game Rules
- Spy count validation logic in `SettingsActivity.kt`
- Timer behavior in `GameActivity.kt`
- Role assignment algorithm in `GameActivity.initializeGame()`

### UI Theming
- Colors defined in `res/values/colors.xml`
- Styles in `res/values/themes.xml`
- Layouts in `res/layout/` directory

## Troubleshooting

### Common Issues

1. **Build Errors**:
   - Ensure Android SDK is properly installed
   - Check Gradle sync completion
   - Verify target SDK version compatibility

2. **Installation Failures**:
   - Enable USB debugging on device
   - Check device compatibility (API 24+)
   - Ensure sufficient storage space

3. **Runtime Issues**:
   - Check LogCat for error messages
   - Verify permissions in AndroidManifest.xml
   - Test with different device configurations

### Performance Considerations
- App optimized for devices with API 24+
- Minimal memory footprint
- Efficient timer and animation handling
- Proper resource cleanup on activity destruction

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes with appropriate tests
4. Submit a pull request with detailed description

## License

This project is open source and available under the [MIT License](LICENSE).

## Support

For issues, questions, or contributions, please:
1. Check existing issues in the repository
2. Create a new issue with detailed description
3. Include device information and Android version
4. Provide steps to reproduce any bugs

---

**Enjoy playing Spy Game!** 🕵️‍♂️🎮
