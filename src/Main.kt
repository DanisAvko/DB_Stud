import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.util.*

fun main() {
    val c: Connection = DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/studentsbd?serverTimezone=UTC",
        "bash",
        "123"
    )
    val s: Statement = c.createStatement()

    val dt1 = "drop table if exists `student`"
    val dt2 = "drop table if exists `subject`"
    val dt3 = "drop table if exists `mark`"
    s.execute(dt3)
    s.execute(dt1) //выполнить
    s.execute(dt2)

    val ct1: String = "create table if not exists `student` (" +
            "id int auto_increment primary key, " +
            "name varchar(30) not null, " +
            "surname varchar(30) not null, " +
            "patronymic varchar(30), " +
            "group_num varchar(6) not null, " +
            "birth date not null, " +
            "start_date year not null" +
            ");"

    val ct2: String = "create table if not exists `subject` ("+
            "id int auto_increment primary key, "+
            "name varchar(30) not null, "+
            "semester int not null, "+
            "control enum('Зачет', 'Диф.зачет', 'Экзамен') not null, "+
            "hours int not null "+
            ");"

    val ct3: String = "create table if not exists `mark` (" +
            "id int auto_increment primary key, " +
            "stud_id int not null, " +
            "subj_id int not null, " +
            "mark int not null, " +
            "constraint `stud` foreign key (`stud_id`) references `student` (`id`), " +
            "constraint `subj` foreign key (`subj_id`) references `subject` (`id`) " +
            ");"
    s.execute(ct1)
    s.execute(ct2)
    s.execute(ct3)

    val files: List<String> = listOf("student.csv", "subject.csv", "mark.csv")
    for (f: String in files) {
        val br = BufferedReader(
            InputStreamReader(
                FileInputStream(f)
            )
        )
        val tb1 = f.split(".")[0]
        var first = true
        var cols = listOf<String>()
        while (br.ready()) {
            val l = br.readLine()
            if (first && l != null) {
                first = false
                cols = l.split(";")
                continue
            }
            if (l != null) {
                val vals = l.split(";")
                var q = "INSERT INTO `$tb1` ("
                for (i in 0 until cols.size) {
                    q += "`${cols[i]}`"
                    if (i < cols.size - 1) q += ", "
                }
                q += ") VALUES ("
                for (i in 0 until vals.size) {
                    q += "'${vals[i]}'"
                    if (i < vals.size - 1) q += ", "
                }
                q += ");"
                s.execute(q)
            } else break
        }
    }

    //Список студенов определенной группы
    /*val sc = Scanner(System.`in`)
    val group_num = sc.next()
    val sq1 = "SELECT name,surname,patronymic " +
            "FROM `student` " +
            "WHERE group_num='$group_num' " +
            "ORDER BY surname, name, patronymic;"
    val result1 = s.executeQuery(sq1)
    while (result1.next()) {
        print(result1.getString("surname"))
        print(" ")
        print(result1.getString("name"))
        print(" ")
        print(result1.getString("patronymic"))
        println()
    }*/

    //Вывод среднего балла судента
    val sq2 = "SELECT student.name, student.surname, student.patronymic, AVG(mark) "+
            "FROM `student` "+
            "INNER JOIN `mark` "+
            "ON student.id=mark.stud_id "+
            "GROUP BY mark.stud_id;"
    val result2 = s.executeQuery(sq2)
    while (result2.next()) {
        print(result2.getString("surname"))
        print(" ")
        print(result2.getString("name"))
        print(" ")
        print(result2.getString("patronymic"))
        print(" ")
        print(result2.getString("AVG(mark)"))
        println()
    }

}


/*var groupNum = Scanner(System.`in`).next()
var SQLrequest = "SELECT * FROM `students` WHERE `Student_group` ='$groupNum'"
val result = s.executeQuery(SQLrequest)
while (result.next()){
    print(result.getString("SURNAME"))
    print(" ")
    print(result.getString("NAME"))
    print(" ")
    print(result.getString("FATHERNAME"))
    println()
}
println()*/



//select student.name,student.surname from student
//where not exists (select stud_id from mark where mark<71 and mark.stud_id = student.id)