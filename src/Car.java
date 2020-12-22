import java.io.Serializable;

public class Car implements Serializable {
    private String reg;
    private int year;
    private String color1;
    private String color2;
    private String color3;
    private String make;
    private String model;
    private int price;
    private int quantity;

    public int getYear() {
        return year;
    }

    public String getColor1() {
        return color1;
    }

    public String getColor2() {
        return color2;
    }

    public String getColor3() {
        return color3;
    }

    public String getColors() {
        StringBuffer s = new StringBuffer();
        if ((color1 != null)) {
            s.append(color1).append(",");
        } else {
            s.append(",");
        }
        if ((color2 != null)) {
            s.append(color2).append(",");
        } else {
            s.append(",");
        }
        if ((color3 != null)) {
            s.append(color3);
        } else {
            s.append("");
        }
        return s.toString();
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public Car(String s) {
        String[] t = s.split(",");

        this.reg = t[0];
        this.year = Integer.parseInt(t[1]);
        this.color1 = t[2];
        this.color2 = t[3];
        this.color3 = t[4];
        this.make = t[5];
        this.model = t[6];
        this.price = Integer.parseInt(t[7]);
        this.quantity = Integer.parseInt(t[8]);
    }


    public Car(String reg, int year, String color1, String color2, String color3, String make, String model, int price, int quantity) {
        this.reg = reg;
        this.year = year;
        this.color1 = color1;
        this.color2 = color2;
        this.color3 = color3;
        this.make = make;
        this.model = model;
        this.price = price;
        this.quantity = quantity;
    }

    public String getReg() {
        return reg;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public void printInfo() {
        Debug.debug(toString());
        System.out.println("***Printing Car Info***");
        System.out.println("Registration Number: " + reg);
        System.out.println("Year Made: " + year);
        System.out.println("Colour: " + color1 + "," + color2 + "," + color3);
        System.out.println("Car Make: " + make);
        System.out.println("Car Model: " + model);
        System.out.println("Price: $" + price);
        System.out.println("Quantity: " + quantity);
        System.out.println();
    }

    public String toString() {
        return reg + "," + year + "," + color1 + "," + color2 + "," + color3 + "," + make + "," + model + "," + price;
    }
}
