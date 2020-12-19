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


    public String getPassword(String username) throws SQLException {
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

    public boolean buyCar(String reg) throws SQLException {
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

    void showAllUsers() throws SQLException {
        setConnection();

        sql = "SELECT * FROM users";
        ps = connection.prepareStatement(sql);
        res = ps.executeQuery();
        while (res.next()) {
            Debug.debug("userId: " + res.getString("userId") +
                    ", username: " + res.getString("username") +
                    ", password: " + res.getString("password"));
        }

        close();
    }

    void showAllCars() throws SQLException {
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

        for (var car : cars) {
            car.printInfo();
        }

        close();
    }

    public Car getCar(String reg) throws SQLException {
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
            car.printInfo();
        }

        close();
        return car;
    }

    public ArrayList<Car> getAllCars() throws SQLException {
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

    public static void main(String[] args) throws SQLException {
        Database db = Database.getDatabase();
        db.buyCar("1YX98J");
    }
}
