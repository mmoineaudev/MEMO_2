# ActivityTracker GUI Application - Development Checklist

## Overall Goal

Create a Maven-based Java Swing GUI application. The application serves as a timestamped notebook for daily activity logging with automatic time tracking per subject and date range.

Use git commit often.

Update this document as a marker for your advancement. 

## Main Architectural and Technical Guidelines

- **Build System**: Maven project structure with standard directory layout
- **UI Framework**: Java Swing with modern look and feel
- **Data Model**: CSV-based storage maintaining backward compatibility with existing file format
- **Architecture**: MVC pattern (Model-View-Controller) for separation of concerns
- **File Storage**: Configurable directory (default: `./log`), auto-created if missing
- **Date Format**: `dd/MM/yyyy HH:mm` for timestamps
- **File Naming Pattern**: `{project}_tracking_{yyyyMMdd}.csv`
- **CSV Separator**: `;` (semicolon)
- **Time Calculation**: Proportional day units (1 day = 7.75 hours = 465 minutes)

## Scope Definition

The solution encompasses:
- Complete GUI application with main window and multiple dialogs
- CSV file management (read, write, list, create)
- Activity entry CRUD operations
- Search and filter functionality
- Time calculations (daily, weekly, per activity type, per description)
- Configuration management
- Data persistence and loading

## Cross-Reference Matrix

| User Input Feature | Use Case(s) |
|-------------------|-------------|
| Transcode to Maven Java Swing GUI | UC01, UC02 |
| Display files ordered by date DESC | UC03 |
| New entry edition with large text areas | UC04 |
| Reuse last 10 distinct activity descriptions | UC05 |
| Search functionality on all CSV columns | UC06 |
| Search results in popup with time sum | UC07 |
| Whole history always opened | UC03 |
| Time sums per activity description per day | UC08 |
| Time sums per activity description per week | UC09 |
| Time sums accessible in popup | UC08, UC09 |
| Resizable panels | UC02 |
| Configurable CSV storage folder | UC10 |
| Auto-create storage folder if missing | UC10 |

---

# Use Case: UC01 Initialize Maven Project Structure

* [x] Create Maven pom.xml with Java Swing dependencies 
* [x] Set up standard Maven directory structure (src/main/java, src/main/resources) 
* [x] Configure Java version (17) in pom.xml 
* [x] Test: Verify `mvn clean compile` succeeds 

## CHARACTERISTIC INFORMATION

* **Goal in Context**: Establish the foundation for the Java Swing application
* **Scope**: Project setup and build configuration
* **Level**: Technical
* **Preconditions**: Maven installed, Java JDK available
* **Success End Condition**: Maven project builds successfully
* **Failed End Condition**: Build errors or missing dependencies
* **Primary Actor**: Developer
* **Trigger**: Project initialization

### MAIN SUCCESS SCENARIO

1. Create project directory structure
2. Generate pom.xml with required dependencies (Swing, optional: Lombok, logging)
3. Configure source/target encoding as UTF-8
4. Add application launcher configuration
5. Verify build with `mvn clean compile`

### EXTENSIONS

1. **Step 2** - Dependency conflict : Use dependency management to resolve version conflicts
2. **Step 5** - Build fails : Review error logs and adjust pom.xml configuration

### RELATED INFORMATION

* **Priority**: Critical - Foundation for all other work
* **Performance Target**: < 1 minute setup
* **Frequency**: One-time

---

# Use Case: UC02 Design and Implement Main GUI Window

* [x] Create main JFrame with system look and feel 
* [x] Implement resizable panels using JSplitPane (three-way layout) 
* [x] Add menu bar for main actions (New Entry, Search, Daily Summary, Weekly Summary, Settings) 
* [x] Status bar showing current file and total entries count 
* [x] Test: Verify window is resizable and panels adjust correctly 

## CHARACTERISTIC INFORMATION

* **Goal in Context**: Provide the primary user interface for the application
* **Scope**: UI layout and window management
* **Level**: Technical/Functional
* **Preconditions**: Maven project structure exists
* **Success End Condition**: Main window displays with all panels resizable
* **Failed End Condition**: Layout breaks on resize or panels not functional
* **Primary Actor**: End User
* **Trigger**: Application startup

### MAIN SUCCESS SCENARIO

1. Launch application
2. Main window appears with default size (e.g., 1200x800)
3. Left panel shows file list (existing CSV files)
4. Center panel shows activity entries from selected file
5. Right panel shows details/summary
6. User can resize panels using split pane dividers
7. Menu/toolbar provides access to all major functions

### EXTENSIONS

1. **Step 2** - Window size not persisted : Implement window state saving/loading
2. **Step 6** - Panels not resizable : Use proper layout managers (JSplitPane)

### RELATED INFORMATION

* **Priority**: Critical - Core user experience
* **Performance Target**: Window loads in < 2 seconds
* **Frequency**: Every application session

---

# Use Case: UC03 Load and Display All Activity Files

* [x] Implement file scanner to find all CSV files matching pattern 
* [x] Sort files by date in descending order (newest first) 
* [x] Display file list in a selectable panel 
* [x] Load and display entries from selected file 
* [x] Show entire history across all files when no filter applied
* [x] Test: Verify files appear sorted correctly and entries load 

## CHARACTERISTIC INFORMATION

* **Goal in Context**: Provide access to complete activity history
* **Scope**: File management and data loading
* **Level**: Functional
* **Preconditions**: Storage directory exists with CSV files
* **Success End Condition**: All files displayed sorted by date DESC, entries visible
* **Failed End Condition**: Files not found or entries not displayed
* **Primary Actor**: End User
* **Trigger**: Application startup or storage folder change

### MAIN SUCCESS SCENARIO

1. Application scans configured storage directory
2. Finds all files matching pattern `{project}_tracking_*.csv`
3. Parses dates from filenames
4. Sorts files descending by date
5. Displays file list in left panel
6. Loads entries from most recent file by default
7. Displays entries in center panel with all fields visible

### EXTENSIONS

1. **Step 1** - Directory doesn't exist : Create directory automatically
2. **Step 2** - No files found : Display empty state message
3. **Step 4** - Invalid date format in filename : Skip file and log warning

### SUB-VARIATIONS

* **Step 5** - File list display options:
  - Show filename only
  - Show filename with date
  - Show filename with entry count
  - Show all of the above

### RELATED INFORMATION

* **Priority**: Critical - Primary data access
* **Performance Target**: < 1 second for 100 files
* **Frequency**: Every application session

---

# Use Case: UC04 Create New Activity Entry

* [x] Implement "New Entry" dialog/modal window
* [x] Create large text areas for description and comment fields
* [x] Add activity type selection (combo box with predefined values)
* [x] Add status selection (TODO, DOING, DONE, NOTE)
* [x] Add time spent input with validation
* [x] Auto-populate timestamp with current date/time
* [x] Implement save confirmation
* [x] Test: Verify entry saves correctly to CSV file

## CHARACTERISTIC INFORMATION

* **Goal in Context**: Allow users to log new activities
* **Scope**: Data entry and validation
* **Level**: Functional
* **Preconditions**: Main window is open, current file is selected
* **Success End Condition**: Entry saved to CSV file with confirmation
* **Failed End Condition**: Validation errors or save failure
* **Primary Actor**: End User
* **Trigger**: User clicks "New Entry" button/menu

### MAIN SUCCESS SCENARIO

1. User opens New Entry dialog
2. Dialog displays all input fields
3. Activity type dropdown shows options: DEV, TEST, CEREMONY, LEARNING, CONTINUOUS_IMPROVEMENT, SUPPORT, ADMIN, DOCUMENTATION
4. Status dropdown shows: TODO, DOING, DONE, NOTE
5. Description text area is large (min 5 rows)
6. Comment text area is large (min 5 rows)
7. Time spent field accepts decimal values (0.125, 0.25, 0.5, etc.)
8. Timestamp auto-filled with current date/time
9. User fills required fields
10. User clicks Save
11. System validates input
12. System appends entry to current day's CSV file
13. System displays success message
14. Entry appears in main list

### EXTENSIONS

1. **Step 11** - Validation fails : Display specific error messages per field
2. **Step 12** - File write fails : Display error and retry option
3. **Step 7** - Invalid time value : Show helper text with examples

### SUB-VARIATIONS

* **Step 3** - Activity type options:
  - DEV
  - TEST
  - CEREMONY
  - LEARNING
  - CONTINUOUS_IMPROVEMENT
  - SUPPORT
  - ADMIN
  - DOCUMENTATION

* **Step 12** - Time value examples:
  - 1h = 0.125
  - 1h20 = 0.2
  - 1h45 = 0.25
  - 2h10 = 0.3
  - 3h3 = 0.5
  - 5h20 = 0.75
  - 7h = 1.0

### RELATED INFORMATION

* **Priority**: Critical - Core functionality
* **Performance Target**: Dialog opens in < 500ms, save in < 1s
* **Frequency**: Multiple times per day

---

# Use Case: UC05 Suggest Last 10 Distinct Activity Descriptions

* [x] Track previously used activity descriptions
* [x] Extract last 10 distinct descriptions from all files
* [x] Display suggestions in clickable list
* [x] Allow user to select a suggestion to auto-fill description field
* [x] Test: Verify suggestions appear and can be selected

## CHARACTERISTIC INFORMATION

* **Goal in Context**: Speed up data entry by reusing previous descriptions
* **Scope**: Input assistance
* **Level**: Functional
* **Preconditions**: At least one previous entry exists
* **Success End Condition**: User can quickly select from previous descriptions
* **Failed End Condition**: No suggestions or suggestions not working
* **Primary Actor**: End User
* **Trigger**: User focuses on description field in New Entry dialog

### MAIN SUCCESS SCENARIO

1. User opens New Entry dialog
2. User clicks/focuses description field
3. System queries all CSV files for distinct descriptions
4. System sorts by most recent usage
5. System displays top 10 distinct descriptions as suggestions
6. User selects a suggestion
7. Description field auto-fills with selected text
8. User can modify or accept as-is

### EXTENSIONS

1. **Step 3** - No previous entries : Show empty state or placeholder
2. **Step 5** - Less than 10 distinct : Show all available
3. **Step 7** - User wants custom : Allow manual editing after selection

### RELATED INFORMATION

* **Priority**: High - Significant productivity improvement
* **Performance Target**: Suggestions appear in < 300ms
* **Frequency**: Every new entry creation

---

# Use Case: UC06 Search Across All CSV Columns

* [x] Implement search dialog with multiple search fields (timestamp, type, description, status, comment)
* [x] Allow search across all columns simultaneously
* [x] Support partial text matching
* [x] Support AND/OR logic for combining conditions
* [x] Apply search to all files
* [x] Test: Verify search returns correct results

## CHARACTERISTIC INFORMATION

* **Goal in Context**: Enable efficient finding of specific activities
* **Scope**: Search and filtering
* **Level**: Functional
* **Preconditions**: Main window is open with loaded data
* **Success End Condition**: Search results displayed matching criteria
* **Failed End Condition**: No results or incorrect results
* **Primary Actor**: End User
* **Trigger**: User clicks "Search" button/menu

### MAIN SUCCESS SCENARIO

1. User opens Search dialog
2. Dialog shows search input field
3. Dialog shows checkboxes for searchable columns
4. All columns selected by default
5. User enters search term(s)
6. User clicks Search
7. System searches across selected columns in all files
8. System collects matching entries
9. System displays results in results panel

### EXTENSIONS

1. **Step 7** - No matches found : Display "No results found" message
2. **Step 7** - Many matches (>100) : Paginate or limit results with warning
3. **Step 5** - Multiple terms : Support AND (default) and OR logic toggle

### SUB-VARIATIONS

* **Step 3** - Searchable columns:
  - Activity Type
  - Description
  - Status
  - Comment
  - Timestamp
  - Time Spent
  - All (default)

* **Step 6** - Search modes:
  - Contains (default)
  - Starts with
  - Ends with
  - Exact match
  - Regex (advanced)

### RELATED INFORMATION

* **Priority**: Critical - Core functionality per user requirements
* **Performance Target**: Search completes in < 1 second for 1000 entries
* **Frequency**: Multiple times per day

---

# Use Case: UC07 Display Search Results with Time Sum

* [x] Create search results dialog
* [x] Display matching entries in scrollable table
* [x] Calculate and display total time spent for matching entries
* [x] Show time breakdown by activity type
* [x] Allow copying entry details
* [x] Test: Verify results display correctly with accurate time sums

## CHARACTERISTIC INFORMATION

* **Goal in Context**: Present search findings with useful aggregations
* **Scope**: Results display and interaction
* **Level**: Functional
* **Preconditions**: Search has been performed (UC06)
* **Success End Condition**: Results displayed with time calculations
* **Failed End Condition**: Results incomplete or time sums wrong
* **Primary Actor**: End User
* **Trigger**: Search completes successfully

### MAIN SUCCESS SCENARIO

1. Search results ready
2. Results popup/dialog opens
3. Scrollable table shows all matching entries
4. Each row displays: timestamp, activity type, description, status, time spent
5. Header shows total matching entries count
6. Footer shows total time spent (in days and hours)
7. Time breakdown by activity type displayed
8. User can select entries to copy details
9. User can close popup when done

### EXTENSIONS

1. **Step 6** - No time data in results : Show "No time tracked" message
2. **Step 8** - User wants to export : Provide export to clipboard/file option
3. **Step 3** - Many results : Implement pagination or virtual scrolling

### RELATED INFORMATION

* **Priority**: Critical - Directly requested feature
* **Performance Target**: Results display in < 500ms
* **Frequency**: Multiple times per day

---

# Use Case: UC08 Calculate and Display Daily Time Sums

* [x] Implement time calculation per day
* [x] Calculate total time spent per activity description per day
* [x] Calculate total time spent per activity type per day
* [x] Display daily summary in dedicated popup/panel
* [x] Convert proportional days to hours (1 day = 7.75 hours)
* [x] Test: Verify calculations match expected values

## CHARACTERISTIC INFORMATION

* **Goal in Context**: Provide daily time tracking visibility
* **Scope**: Time calculation and reporting
* **Level**: Functional
* **Preconditions**: Entries with time data exist
* **Success End Condition**: Accurate daily time sums displayed
* **Failed End Condition**: Wrong calculations or missing data
* **Primary Actor**: End User
* **Trigger**: User requests time summary or views entries

### MAIN SUCCESS SCENARIO

1. User requests daily time summary
2. System identifies current day's file
3. System reads all entries from file
4. System groups entries by activity description
5. System sums time spent per description
6. System groups entries by activity type
7. System sums time spent per type
8. System calculates grand total
9. System displays summary with:
   - Per-description breakdown
   - Per-type breakdown
   - Total in days and hours
   - Minutes conversion for small values
10. User can close summary when done

### EXTENSIONS

1. **Step 3** - File doesn't exist : Create empty summary for today
2. **Step 5** - Invalid time values : Skip invalid entries, log warning
3. **Step 9** - User wants historical : Allow date selection for past days

### RELATED INFORMATION

* **Priority**: Critical - Core time tracking feature
* **Performance Target**: Calculation in < 500ms
* **Frequency**: Multiple times per day

---

# Use Case: UC09 Calculate and Display Weekly Time Sums

* [x] Implement week boundary detection (week number calculation)
* [x] Calculate total time spent per activity description per week
* [x] Calculate total time spent per activity type per week
* [x] Display weekly summary in dedicated popup/panel
* [x] Show per-day breakdown within the week
* [x] Test: Verify weekly calculations are accurate

## CHARACTERISTIC INFORMATION

* **Goal in Context**: Provide weekly time tracking visibility (NEW FEATURE)
* **Scope**: Time calculation and reporting
* **Level**: Functional
* **Preconditions**: Entries with time data exist across multiple days
* **Success End Condition**: Accurate weekly time sums displayed
* **Failed End Condition**: Wrong calculations or missing days
* **Primary Actor**: End User
* **Trigger**: User requests weekly time summary

### MAIN SUCCESS SCENARIO

1. User requests weekly time summary
2. System determines current week boundaries
3. System identifies all files within week range
4. System reads entries from all week files
5. System groups entries by activity description
6. System sums time spent per description for week
7. System groups entries by activity type
8. System sums time spent per type for week
9. System calculates per-day totals within week
10. System displays summary with:
    - Weekly totals by description
    - Weekly totals by type
    - Per-day breakdown
    - Grand total in days and hours
11. User can close summary when done

### EXTENSIONS

1. **Step 2** - Week start day preference : Allow configuration (Monday/Sunday)
2. **Step 3** - Missing days in week : Show empty days with zero values
3. **Step 10** - User wants custom range : Allow date range picker

### RELATED INFORMATION

* **Priority**: High - New feature requested by user
* **Performance Target**: Calculation in < 1 second
* **Frequency**: Daily/Weekly review

---

# Use Case: UC10 Configure Storage Directory

* [x] Implement configuration dialog/settings panel
* [x] Allow user to set custom storage directory
* [x] Default value: `./log/`
* [x] Auto-create directory if it doesn't exist
* [x] Validate directory is writable
* [x] Persist configuration between sessions (in-memory for now)
* [x] Test: Verify configuration saves and loads correctly

## CHARACTERISTIC INFORMATION

* **Goal in Context**: Allow flexible storage location
* **Scope**: Configuration management
* **Level**: Functional
* **Preconditions**: Application is running
* **Success End Condition**: Storage directory configured and accessible
* **Failed End Condition**: Directory not writable or configuration lost
* **Primary Actor**: End User
* **Trigger**: First run or Settings menu access

### MAIN SUCCESS SCENARIO

1. Application starts
2. System checks for existing configuration
3. If no config, use default `./log`
4. System checks if directory exists
5. If not exists, create directory
6. System verifies write permissions
7. System stores configuration for future sessions
8. User can change directory via Settings dialog
9. New directory is validated and saved

### EXTENSIONS

1. **Step 5** - Cannot create directory : Display error with suggested alternatives
2. **Step 6** - No write permissions : Display error and suggest admin action
3. **Step 8** - User selects invalid path : Validate and show error message

### SUB-VARIATIONS

* **Step 3** - Default directory options:
  - `./log` (default)
  - User home directory
  - Desktop folder
  - Custom path

### RELATED INFORMATION

* **Priority**: Medium - Important for setup
* **Performance Target**: Directory check in < 100ms
* **Frequency**: Once per installation, occasional changes

---

# Use Case: UC11 Implement Entry Detail View

* [x] Create detail panel showing full entry information
* [x] Display all fields: project, activity type, description, status, comment, timestamp, time spent
* [x] Format time spent as both proportional days and hours/minutes
* [x] Allow clicking entry in list to view details
* [x] Test: Verify all fields display correctly

## CHARACTERISTIC INFORMATION

* **Goal in Context**: Provide complete view of individual entries
* **Scope**: Data display
* **Level**: Functional
* **Preconditions**: Entry list is displayed
* **Success End Condition**: All entry details visible and formatted
* **Failed End Condition**: Missing fields or formatting errors
* **Primary Actor**: End User
* **Trigger**: User selects/clicks an entry

### MAIN SUCCESS SCENARIO

1. User clicks entry in list
2. Detail panel updates
3. All fields displayed with labels
4. Time shown as "0.5 days (~ 3.88 hours)"
5. Long comments scrollable
6. Timestamp in readable format
7. Status shown with color coding

### EXTENSIONS

1. **Step 4** - Zero time : Show "Time not tracked" or "0 days"
2. **Step 5** - Very long comment : Implement text wrapping and scrolling

### RELATED INFORMATION

* **Priority**: Medium - Supporting feature
* **Performance Target**: Detail loads instantly
* **Frequency**: Every entry selection

---

# Use Case: UC12 Implement Entry Edit/Delete Functionality

* [x] Allow editing existing entries
* [x] Allow deleting entries with confirmation
* [x] Maintain edit history or backup before changes
* [x] Update time calculations after edit/delete
* [x] Test: Verify edits save and deletions work safely

## CHARACTERISTIC INFORMATION

* **Goal in Context**: Enable correction of logged activities
* **Scope**: Data modification
* **Level**: Functional
* **Preconditions**: Entry is selected
* **Success End Condition**: Entry modified or deleted successfully
* **Failed End Condition**: Data loss or corruption
* **Primary Actor**: End User
* **Trigger**: User requests edit or delete

### MAIN SUCCESS SCENARIO

1. User selects entry
2. User clicks Edit or Delete button
3. For Edit: Open entry in New Entry dialog pre-filled
4. For Delete: Show confirmation dialog
5. User confirms action
6. System updates/deletes entry in CSV file
7. System refreshes display
8. Time recalculations update automatically

### EXTENSIONS

1. **Step 4** - Delete confirmation declined : Cancel operation
2. **Step 6** - File operation fails : Rollback and show error
3. **Step 3** - User wants to copy : Provide "Duplicate Entry" option

### RELATED INFORMATION

* **Priority**: Medium - Important for corrections
* **Performance Target**: Operation completes in < 500ms
* **Frequency**: Occasional

---

# Use Case: UC13 Implement Date Range Filtering

* [x] Add date range picker to main view
* [x] Allow filtering entries by date range
* [x] Show entry count for filtered view
* [x] Maintain filter state during session
* [x] Test: Verify filtering works correctly across files

## CHARACTERISTIC INFORMATION

* **Goal in Context**: Enable focused viewing of specific time periods
* **Scope**: Filtering and navigation
* **Level**: Functional
* **Preconditions**: Main window is open
* **Success End Condition**: Entries filtered by date range
* **Failed End Condition**: Wrong entries shown or filter doesn't work
* **Primary Actor**: End User
* **Trigger**: User wants to view specific period

### MAIN SUCCESS SCENARIO

1. User opens date range filter
2. User selects start date
3. User selects end date
4. User applies filter
5. System loads entries from matching files
6. System filters entries by timestamp
7. System displays filtered entries
8. System shows count of filtered entries
9. User can clear filter to see all entries

### EXTENSIONS

1. **Step 2** - Quick presets : Add "Today", "This Week", "Last Week", "This Month"
2. **Step 7** - No entries in range : Show empty state message
3. **Step 9** - User wants to save filter : Allow named filter presets

### RELATED INFORMATION

* **Priority**: Medium - Useful for analysis
* **Performance Target**: Filter applies in < 500ms
* **Frequency**: Occasional

---

# Use Case: UC14 Implement Export Functionality

* [ ] Add export to CSV option
* [ ] Add export to text/report option
* [ ] Allow exporting filtered or all entries
* [ ] Include time summaries in export
* [ ] Test: Verify exports are correctly formatted

## CHARACTERISTIC INFORMATION

* **Goal in Context**: Enable data extraction for external use
* **Scope**: Data export
* **Level**: Functional
* **Preconditions**: Entries are loaded
* **Success End Condition**: Export file created with correct data
* **Failed End Condition**: Export fails or data incorrect
* **Primary Actor**: End User
* **Trigger**: User requests export

### MAIN SUCCESS SCENARIO

1. User selects Export from menu
2. System shows export options dialog
3. User selects format (CSV, Text Report)
4. User selects scope (All, Filtered, Date Range)
5. User selects destination file
6. System generates export
7. System displays success message
8. File contains entries and time summaries

### EXTENSIONS

1. **Step 3** - Additional formats : Add JSON, HTML options
2. **Step 6** - Export fails : Show error and allow retry
3. **Step 8** - User wants email : Add email integration option

### RELATED INFORMATION

* **Priority**: Low - Nice to have
* **Performance Target**: Export in < 2 seconds for 1000 entries
* **Frequency**: Occasional (weekly/monthly)

---

# Use Case: UC15 Implement Application Icon and Look and Feel

* [ ] Create application icon
* [ ] Set system-appropriate look and feel (Nimbus or system default)
* [ ] Apply consistent color scheme matching original CLI colors
* [ ] Ensure proper window title and taskbar icon
* [ ] Test: Verify application appearance is professional

## CHARACTERISTIC INFORMATION

* **Goal in Context**: Provide polished user experience
* **Scope**: UI polish and branding
* **Level**: Technical/UX
* **Preconditions**: Main window implemented
* **Success End Condition**: Application looks professional and consistent
* **Failed End Condition**: Ugly or inconsistent appearance
* **Primary Actor**: Developer/Designer
* **Trigger**: UI implementation phase

### MAIN SUCCESS SCENARIO

1. Application launches
2. Window shows custom icon
3. Look and feel matches OS conventions
4. Colors consistent with original (green for activity, blue for description, red for status)
5. Window title shows "ActivityTracker"
6. Font sizes readable and consistent

### EXTENSIONS

1. **Step 3** - Custom theme : Allow user to select theme
2. **Step 4** - High DPI : Ensure proper scaling on high-resolution displays

### RELATED INFORMATION

* **Priority**: Low - Cosmetic
* **Performance Target**: No impact on load time
* **Frequency**: One-time setup
