package epl.learning.ninth.ContainedEventSelection;

/**
 * Created by wxmimperio on 2015/9/10.
 */
public class Review {
    private int reviewId;
    private String comment;

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String toString() {
        return "Review{" + "reviewId=" + reviewId + ", comment='" + comment + '\'' + '}';
    }
}
