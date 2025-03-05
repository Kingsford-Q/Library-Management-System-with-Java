const { MongoClient, ObjectId } = require('mongodb');
const MONGO_URI = process.env.MONGO_URI;

const { getBooksCollection } = require('../db');

// Fetch all books
async function getAllBooks(req, res) {
    try {
        const booksCollection = await getBooksCollection();
        const books = await booksCollection.find().toArray();
        res.status(200).json(books);
    } catch (error) {
        console.error('Error fetching books:', error);
        res.status(500).json({ message: 'Error fetching books', error: error.message });
    }
}

// Add a new book
async function addBook(req, res) {
    try {
        const booksCollection = await getBooksCollection();
        const { title, author, isbn, category } = req.body;

        // Validate inputs
        if (!title || !author || !isbn || !category) {
            return res.status(400).json({ message: "All fields are required" });
        }

        // Example ISBN validation (you can use a more complex one depending on your requirements)
        const isbnRegex = /^(97(8|9))?\d{9}(\d|X)$/;
        if (!isbnRegex.test(isbn)) {
            return res.status(400).json({ message: "Invalid ISBN format" });
        }

        await booksCollection.insertOne({ title, author, isbn, category });
        res.status(201).json({ message: "Book added successfully" });
    } catch (error) {
        console.error(error);  // Only log in development environment
        res.status(500).json({
            message: "Error adding book",
            error: process.env.NODE_ENV === 'production' ? "Internal Server Error" : error.message
        });
    }
}

// Edit a book
async function editBook(req, res) {
    try {
        const booksCollection = await getBooksCollection();
        const { id } = req.params;
        const { title, author, isbn, category } = req.body;

        if (!ObjectId.isValid(id)) {
            return res.status(400).json({ message: "Invalid book ID format" });
        }

        // Only update provided fields
        const updateFields = {};
        if (title) updateFields.title = title;
        if (author) updateFields.author = author;
        if (isbn) updateFields.isbn = isbn;
        if (category) updateFields.category = category;

        if (Object.keys(updateFields).length === 0) {
            return res.status(400).json({ message: "No fields to update" });
        }

        const result = await booksCollection.updateOne(
            { _id: new ObjectId(id) },
            { $set: updateFields }
        );

        if (result.matchedCount === 0) {
            return res.status(404).json({ message: "Book not found" });
        }

        res.json({ message: "Book updated successfully" });
    } catch (error) {
        console.error(error); // Log for debugging
        res.status(500).json({
            message: "Error updating book",
            error: process.env.NODE_ENV === 'production' ? "Internal Server Error" : error.message
        });
    }
}

// Delete a book
async function deleteBook(req, res) {
    try {
        const booksCollection = await getBooksCollection();
        const { id } = req.params;

        if (!ObjectId.isValid(id)) {
            return res.status(400).json({ message: "Invalid book ID format" });
        }

        const result = await booksCollection.deleteOne({ _id: new ObjectId(id) });

        if (result.deletedCount === 0) {
            return res.status(404).json({ message: "Book not found" });
        }

        res.json({ message: "Book deleted successfully" });
    } catch (error) {
        console.error(error);  // Only log in development environment
        res.status(500).json({
            message: "Error deleting book",
            error: process.env.NODE_ENV === 'production' ? "Internal Server Error" : error.message
        });
    }
}

module.exports = {
    getAllBooks,
    addBook,
    editBook,
    deleteBook
};
