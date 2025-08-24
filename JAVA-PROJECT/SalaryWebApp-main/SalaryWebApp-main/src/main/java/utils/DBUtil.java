package utils;

import java.sql.*;

public class DBUtil {
    static Connection connection = null;
    static Statement statement = null;

    public static void connect () {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/job_salary", "root", "Password123#@!"
            );
            statement = connection.createStatement();
            System.out.println("Connection is worked!");
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Connection is failed");
            throw new RuntimeException(e);
        }
    }

    public static ResultSet selectUser (String username, String password) throws SQLException {
        return statement.executeQuery("select * from members where username = '"+username+"' and password = '"+password+"'");
    }

    public static ResultSet selectAll (String tableName) throws SQLException {
        return statement.executeQuery("select * from "+tableName);
    }

    public static boolean register (String tableName, String username, String password) {
        String query = "insert into " + tableName + " (username, password, role) values ('"+ username + "', '" + password +"', 'user')";
        System.out.println(query);
        try {
            if (statement.executeUpdate(query)>0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean insertUser (String tableName, int age, String gender, String education_level, float years_of_experience, int salary) {
        // create query
//        String query = "insert into " + tableName + " values (" + id + ", " + age + ", '" + gender + "', '" + education_level + "', "+ years_of_experience +", "+ salary +")";
        String query = "insert into " + tableName + " (age, gender, education_level, years_of_experience, salary) values (" + age + ", '" + gender + "', '" + education_level + "', "+ years_of_experience +", "+ salary +")";
        try {
            if (statement.executeUpdate(query) > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public static boolean insertJob (String tableName, String jobTitle) {
        String query = "insert into " + tableName + " (job_title) values ('" + jobTitle + "')";
        try {
            if (statement.executeUpdate(query) > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean insertRelation (String tableName, int id, int jobId) {
        String query = "insert into " + tableName + " ( user_id, job_id )" + " values (" + id + ", " + jobId + ")";
        try {
            if (statement.executeUpdate(query)>0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean update (String tableName, int id, int age, String gender, String education_level, float years_of_experience, int salary) {
        String query = "update "+tableName+" set age = '" + age + "', gender = '" + gender + "', education_level = '" + education_level +"', years_of_experience = '"+ years_of_experience +"', salary = '"+ salary +"' where id = "+id;
        System.out.println(query);
        try {
            if (statement.executeUpdate(query)>0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean updateJob (String tableName, int id, String job_title) {
        String query = "update "+tableName+" set job_title = '" + job_title +"' where job_id = "+id;
        System.out.println(query);
        try {
            if (statement.executeUpdate(query)>0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean updateRelation (String tableName, int id, int user_id, int job_id) {
        String query = "update "+tableName+" set user_id = '" + user_id +"', job_id = '"+ job_id+"' where id = "+id;
        System.out.println(query);
        try {
            if (statement.executeUpdate(query)>0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean delete (String tableName1, String tableName2,  int id) {
        String query = "DELETE " + tableName1 + ", "+ tableName2 + " from "+ tableName1 +
                " JOIN "+ tableName2 +" ON salary_datas.user_id = users.id where users.id = " + id;
        System.out.println(query);
        try {
            if (statement.executeUpdate(query)>0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean deleteUser (String tableName1,  int id) {
        String query = "DELETE from "+ tableName1 + " where id = " + id;
        System.out.println(query);
        try {
            if (statement.executeUpdate(query)>0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean deleteJob (String tableName1,  int id) {
        String query = "DELETE from "+ tableName1 + " where job_id = " + id;
        System.out.println(query);
        try {
            if (statement.executeUpdate(query)>0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean deleteRelation (String tableName1,  int id) {
        String query = "DELETE from "+ tableName1 + " where id = " + id;
        System.out.println(query);
        try {
            if (statement.executeUpdate(query)>0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean findId (String tableName, int id) {
        String query = "select * from " + tableName + " where = "+id;
        try {
            if (statement.executeUpdate(query)>0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
