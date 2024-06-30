# EventManager
Android Application for Event Managing

### Requirement and Goals
1. Allow users to create, edit, and delete events.
2. Enable users to set specific dates and times for their events.
3. Provide notification functionality to remind users of upcoming events.
4. Ensure events are saved and persist across app sessions.
5. Deliver a user-centered UI that is intuitve and easy to navigate.


### User Needs Addressed
1. **Event Organization**: Users need a way to organize and keep track of their events in a straightforward manner.
2. **Timely Reminders**: Users need notifications to remind them of their events, ensuring they do not miss important activities.
3. **Ease of Use**: Users require an intuitive interface that makes it easy to create and manage events without unnecessary complexity.


### Screens and Features
#### Screens
1. **Main Screen**: Displays a list of all events added by a user, with options to add new events, edit existing ones, and delete events.
2. **Event Creation Screen**: Allows users to input event details, including the Name, Date, and Time, and set a reminder.
3. **Permissions Screen**: Guides users to grant needed permissions for notifications and alarms.
4. **Log-in Screen**: Allows the creation of new accounts that are added to the database and resuming active user sessions to retain their events.

#### Features
1. **Event List**: Shows all saved events in a RecyclerView.
2. **Event Form**: Uses EditTexts for event name, date, and time, with number pickers for precise input.
3. **Notifications**: Utilizes AlarmManager to schedule notifications for events at the specificed times.
4. **Data Persistence**: Stores events in a SQLite database to ensure they persist across app account sessions.


### UI Design Considerations
1. **Simplicity**: The layout is clean and straightforward, ensuring users can easily navigate the app.
2. **Intuitive Controls**: THe use of number pickers for date and time inputs make it easy for users to set event details accurately.
3. **Visual Clarity**: Labels and prompts guide users through the process of adding and managing events, reducing confusion.

### Development Approach
1. **Initial Planning**: Defined the core features and designed the UI layout.
2. **Iterative Development**: Built the app incrementally, starting with basic functionality and progressively added more features.
3. **Modular Coding**: Implemented features in separate modules to ensure code maintainability and readability.
4. **Testing and Refinement**: Continuously tested the app to identify and fix bugs, ensuring smooth operation.

### Techniques and Strategies
- **Modular Design**: Each feature was developed as a separate module, making it easier to manage and debug.
- **Continuous Testing**: Regular testing was conducted to catch and resolve issues early, ensuring a stable application.
- **User Feedback**: Incorporated feedback from potential users to refine the UI and improve usability.

### Testing Process
1. **Unit Testing**: Tested individual components to verify their correct operation.
2. **Integration Testing**: Ensured that different parts of the app worked together seamlessly.
3. **User Testing**: Conducted tests with real users to gather feedback and identify usability issues.

### Challenges and Innovations
During the development process, my challenge was ensuring that notifications were scheduled accurately via the `AlarmManager` and handling permissions effectively to overcome this challenge.

### Notable Successess
The implementation of notifications was particularly successful. It demonstrated a deep understanding of Android's notification system and effective management of background tasks.
