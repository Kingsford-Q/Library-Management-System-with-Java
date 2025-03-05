const connectDB = require("./db");

async function getBooksCollection() {
    const db = await connectDB();
    return db.collection("books");
}

module.exports = getBooksCollection;
