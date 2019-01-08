package bluebomb.urlshortener.model;

import java.util.Objects;

public class Size {
    private int height;
    private int width;

    public Size(int height, int width) {
        this.height = height;
        this.width = width;
    }


    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Size size = (Size) o;
        return height == size.height &&
                width == size.width;
    }

    @Override
    public int hashCode() {
        return Objects.hash(height, width);
    }
}