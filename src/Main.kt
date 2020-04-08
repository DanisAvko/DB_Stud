import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.util.*

fun main() {
    val c: Connection = DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/stdbd1?serverTimezone=UTC",
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
            "admission year not null" +
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

    val sq4 = "SELECT id,concat(surname,' ',n,'.',p,'.') as fio, mmark, stipendiya " +
            "FROM (select sid as id,surname,n,p,min(mark) as mmark, IF(min(mark)=4,1800,IF(min(mark)=5,2600,0)) as stipendiya from" +
            "(Select student.id as sid, surname, substring(name, 1, 1) as n, substring(patronymic, 1, 1) as p, 2*(year(now())-admission)-if(month(now())=1,2,if(month(now())>=2 and month(now())<=6,1,0)) as sess from student) as studsess " +
            "INNER JOIN(select subj_id, stud_id, if(mark<56,2,if(mark<71,3,if(mark<86,4,5))) as mark, semester from mark inner join subject on subject.id = subj_id) as marks " +
            "ON sid=marks.stud_id group by sid) as stds WHERE mmark>=4"

    val result4 = s.executeQuery(sq4)
    while (result4.next()) {
        print(result4.getString("fio"))
        print(" ")
        print(result4.getString("mmark"))
        print(" ")
        print(result4.getString("stipendiya"))
        println()
    }
}