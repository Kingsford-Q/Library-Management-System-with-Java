require("dotenv").config();
const express = require("express");
const { connectDB } = require('./db');  // This is the correct import
const cors = require("cors");
const bookRoutes = require("./routes/bookRoutes");

const app = express();
const PORT = process.env.PORT || 5000;

// Middleware
app.use(cors());
app.use(express.json());

// Connect to MongoDB (Using Native MongoDB Driver)
connectDB()
  .then(() => console.log("Connected to MongoDB Atlas"))
  .catch((error) => console.error("MongoDB connection error:", error));

// Routes
app.use("/api", bookRoutes);

app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
