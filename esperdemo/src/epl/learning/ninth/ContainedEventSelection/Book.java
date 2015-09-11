package epl.learning.ninth.ContainedEventSelection;

/**
 * Created by wxmimperio on 2015/9/10.
 */

public class Book {
    private int bookId;
    private String author;
    private Review review;

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public String toString() {
        return "Book{" + "bookId=" + bookId + ", author='" + author + '\'' + ", review=" + review + '}';
    }
}