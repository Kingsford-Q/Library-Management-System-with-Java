# Library Management System

## Overview
The **Library Management System** is a JavaFX-based application that allows users to manage a collection of books. It provides features such as adding, editing, deleting, and viewing books. The system integrates with a **MongoDB Atlas** database using a **Node.js backend**, ensuring efficient data storage and retrieval.

## Features
- 📚 **View Books:** Displays a list of books stored in the database.
- 🔍 **Search Books:** Easily find books by title, author, category, or ISBN.
- ➕ **Add Books:** Users can add new books to the database.
- ✏️ **Edit Books:** Modify existing book details.
- ❌ **Delete Books:** Remove books from the collection.
- 🌗 **Light/Dark Mode:** Switchable UI theme for better user experience.

## Technologies Used
### Frontend (JavaFX)
- JavaFX (UI framework)
- FXML (for UI layouts)
- CSS (for styling, with light/dark theme support)

### Backend (Node.js & MongoDB)
- Node.js (server-side runtime)
- Express.js (backend framework for handling API requests)
- MongoDB Atlas (cloud-based NoSQL database)
- Native MongoDB Driver (for database interactions)

## Installation & Setup
### Prerequisites
- Java Development Kit (JDK 17+)
- Node.js & npm
- MongoDB Atlas (or a local MongoDB instance)

### Backend Setup
1. Navigate to the `backend` folder:
   ```sh
   cd backend
   ```
2. Install dependencies:
   ```sh
   npm install
   ```
3. Create a `.env` file and set up your **MongoDB connection string**:
   ```env
   MONGO_URI=your_mongodb_connection_string
   ```
4. Start the backend server:
   ```sh
   node server.js
   ```

### Frontend (JavaFX) Setup
1. Open the JavaFX project in your preferred IDE.
2. Configure the JavaFX runtime (if required).
3. Run the `HelloApplication.java` file.

## JSON Data Sample
A sample **500-books.json** file is included in the project, containing real book titles, authors, categories, and ISBNs. You can use this dataset to populate your database.

## Contributing
Pull requests are welcome. If you find any bugs or want to enhance the system, feel free to contribute!

## License
This project is licensed under the **MIT License**.

