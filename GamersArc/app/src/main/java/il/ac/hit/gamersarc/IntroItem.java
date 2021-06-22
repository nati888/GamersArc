package il.ac.hit.gamersarc;

public class IntroItem {

    int title;
    int image;
    int description1;
    int description2;
    int description3;

    public IntroItem(int title, int image, int description1, int description2, int description3) {
        this.title = title;
        this.image = image;
        this.description1 = description1;
        this.description2 = description2;
        this.description3 = description3;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getDescription1() {
        return description1;
    }

    public void setDescription1(int description1) {
        this.description1 = description1;
    }

    public int getDescription2() {
        return description2;
    }

    public void setDescription2(int description2) {
        this.description2 = description2;
    }

    public int getDescription3() {
        return description3;
    }

    public void setDescription3(int description3) {
        this.description3 = description3;
    }
}
