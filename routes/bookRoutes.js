const express = require('express');
const { getAllBooks, addBook, editBook, deleteBook } = require('../controllers/bookControllers');

const router = express.Router();

// Define API routes
router.get('/books', getAllBooks);
router.post('/books', addBook);
router.put('/books/:id', editBook);
router.delete('/books/:id', deleteBook);

module.exports = router;

