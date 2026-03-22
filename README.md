# MEMO_V2 - Activity Tracker

A desktop Java application for tracking daily work activities with CSV-based persistence.

## Overview

MEMO_V2 is a Swing-based activity tracker that allows users to:
- Create and manage daily activity entries
- Filter by date range (last month through today)
- View daily and weekly summaries grouped by description
- Export/import activity data via CSV files

## Architecture

### Core Components

**Model Layer:**
- `ActivityEntry` - Represents a single activity record with timestamp, type, description, status, comment, and time spent
- `CSVFile` - Manages reading/writing activity entries to/from CSV format (semicolon-delimited)
- `ActivityTracker` - Utility for finding tracking files and retrieving recent descriptions

**View Layer:**
- `MainFrame` - Main application window with file list, entry table, and detail panel
- `NewEntryDialog` - Form for adding new activity entries
- `SummaryDialog` - Dialog for generating daily/weekly summaries
- `DateRangeDialog` - Date range filter dialog (text-based input)
- `SettingsDialog` - Configuration dialog for storage directory

**Controller:**
- `Main` - Application entry point, initializes Swing look-and-feel

### File Format

Tracking files follow the pattern: `{project}_tracking_YYYYMMDD.csv`

CSV structure (semicolon-delimited):
```
timestamp;activity_type;description;status;comment;time_spent_days
dd/MM/yyyy HH:mm;DEV;Implemented feature;DONE;;0.25
```

Time is stored as proportional days where 1 day = 7.75 working hours.

## Development Notes

### Key Design Decisions

1. **Single-file focus**: Each day's activities are stored in a separate file, sorted with today's file always first
2. **Comment escaping**: Newlines in comments are escaped as `\n` for CSV compatibility
3. **Unified summary dialog**: Daily and weekly summaries use the same dialog with combo box selection
4. **Work-day hours**: Time conversion uses 7.75 hours per day (not standard 8h or 24h)

### Known Issues Resolved

- NullPointerException in MainFrame.createUI (split pane initialization order)
- Layout constraint errors when adding status bar to JSplitPane content pane
- Entry persistence not refreshing UI after save
- DateRangeDialog SpinnerDateModel casting issues (replaced with text fields)
- Initial text area sizing in NewEntryDialog (added setVisible override)

## Running the Application

```bash
cd MEMO_V2
mvn clean compile exec:java -Dexec.mainClass=com.memo_v2.Main
```

Storage directory defaults to `./log` relative to project root.

---

**Co-signed by:**
- **Hermes**: "Fixed the split pane initialization order and layout constraints. Made today's file auto-create and select by default. Comment newlines now properly escaped/unescaped for CSV compatibility."
- **Qwen**: "Unified daily/weekly summaries into a single dialog with description-level grouping. Fixed date range defaults to last month through today. Improved UI sizing with revalidation override."

---

**Release tag:** hermes is pretty good
