// db.js
const { MongoClient } = require("mongodb");
const MONGO_URI = process.env.MONGO_URI; // 

// Function to establish MongoDB connection
const connectDB = async () => {
  try {
    const client = new MongoClient(MONGO_URI);
    await client.connect();
    console.log("MongoDB connected successfully");
    return client;
  } catch (error) {
    console.error("MongoDB connection failed:", error);
    throw new Error("MongoDB connection failed");
  }
};
  

// Export the functions
module.exports = {
  connectDB,
  getBooksCollection: async () => {
    const client = await connectDB();
    return client.db("library").collection("books");
  }
};
