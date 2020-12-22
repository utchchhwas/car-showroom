import java.sql.*;
import java.util.ArrayList;

public class Database {
    private static final Database db = new Database();

    private final String databaseAddress;
    private Connection connection = null;
    private String sql = null;
    private PreparedStatement ps = null;
    private ResultSet res = null;

    private Database() {
        databaseAddress = "jdbc:sqlite:database.db";
    }

    public static Database getDatabase() {
        return db;
    }

    private void setConnection() throws SQLException {
        connection = DriverManager.getConnection(databaseAddress);
    }

    private void close() throws SQLException {
        ps.close();
        connection.close();

        connection = null;
        sql = null;
        ps = null;
        res = null;
    }


    synchronized public String getPassword(String username) throws SQLException {
        setConnection();

        String password = null;

        sql = "SELECT password FROM users WHERE username = ?";
        ps = connection.prepareStatement(sql);
        ps.setString(1, username);
        res = ps.executeQuery();
        if (res.next()) {
            password = res.getString("password");
        }

        close();
        return password;
    }

    synchronized public boolean buyCar(String reg) throws SQLException {
        setConnection();

        boolean flag = false;

        sql = "SELECT quantity FROM cars WHERE reg = ?";
        ps = connection.prepareStatement(sql);
        ps.setString(1, reg);
        res = ps.executeQuery();
        int quantity = res.getInt("quantity");
        if (quantity > 0) {
            sql = "UPDATE cars SET quantity = ? WHERE reg = ?";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, --quantity);
            ps.setString(2, reg);
            ps.execute();
            flag = true;
        }

        close();
        return flag;
    }

    synchronized public Car getCar(String reg) throws SQLException {
        setConnection();

        Car car = null;

        sql = "SELECT * FROM cars WHERE reg = ?";
        ps = connection.prepareStatement(sql);
        ps.setString(1, reg);
        res = ps.executeQuery();
        if (res.next()) {
            car = new Car(res.getString(1), res.getInt(2),
                    res.getString(3), res.getString(4),
                    res.getString(5), res.getString(6),
                    res.getString(7), res.getInt(8),
                    res.getInt(9));
        }

        close();
        return car;
    }

    synchronized public ArrayList<Car> getCar(String make, String model) throws SQLException {
        setConnection();

        ArrayList<Car> cars = new ArrayList<>();

        if (model.equals("")) {
            sql = "SELECT * FROM cars WHERE make = ?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, make);
        }
        else {
            sql = "SELECT * FROM cars WHERE make = ? AND model = ?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, make);
            ps.setString(2, model);
        }
        res = ps.executeQuery();
        while (res.next()) {
            cars.add(new Car(
                    res.getString(1), res.getInt(2),
                    res.getString(3), res.getString(4),
                    res.getString(5), res.getString(6),
                    res.getString(7), res.getInt(8),
                    res.getInt(9))
            );
        }

        close();
        return cars;
    }

    synchronized public ArrayList<Car> getAllCars() throws SQLException {
        setConnection();

        ArrayList<Car> cars = new ArrayList<>();

        sql = "SELECT * FROM cars";
        ps = connection.prepareStatement(sql);
        res = ps.executeQuery();
        while (res.next()) {
            cars.add(new Car(res.getString(1), res.getInt(2),
                    res.getString(3), res.getString(4),
                    res.getString(5), res.getString(6),
                    res.getString(7), res.getInt(8),
                    res.getInt(9)));
        }

        close();
        return cars;
    }

    synchronized public void deleteCar(String reg) throws SQLException {
        setConnection();

        sql = "DELETE FROM cars WHERE reg = ?";
        ps = connection.prepareStatement(sql);
        ps.setString(1, reg);
        ps.execute();

        close();
    }

    synchronized public void addCar(Car car) throws SQLException {
        setConnection();

        sql = "INSERT INTO cars(" +
                "reg,year,color1,color2,color3,make,model,price,quantity) " +
                "VALUES(?,?,?,?,?,?,?,?,?)";
        ps = connection.prepareStatement(sql);
        ps.setString(1, car.getReg());
        ps.setInt(2, car.getYear());
        ps.setString(3, car.getColor1());
        ps.setString(4, car.getColor2());
        ps.setString(5, car.getColor3());
        ps.setString(6, car.getMake());
        ps.setString(7, car.getModel());
        ps.setInt(8, car.getPrice());
        ps.setInt(9, car.getQuantity());
        ps.execute();

        close();
    }
}
