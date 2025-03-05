// db.js
const { MongoClient } = require("mongodb");
const MONGO_URI = process.env.MONGO_URI; // 

// Function to establish MongoDB connection
const connectDB = async () => {
    try {
      const client = await MongoClient.connect(MONGO_URI);
      return client;
    } catch (error) {
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
