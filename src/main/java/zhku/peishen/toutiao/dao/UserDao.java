package zhku.peishen.toutiao.dao;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;
import zhku.peishen.toutiao.model.User;

/**
 * Created by ipc on 2017/7/7.
 */
@Mapper
//@Component
/*@Component:加入IoC容器，可是不用这个注解也没问题，还是能用UserDao的方法，为什么呢？
/想法：
    UserDao是接口来着，不能实例化加入IoC容器，所以@Component也没什么作用
*/
public interface UserDao {
    String TABLE_NAME = "user";
    String INSERT_FIELDS = " name,password,salt,head_url ";
    String SELECT_FIELDS = " id,name,password,salt,head_url ";
    //insert into user(name,p...) values()
    @Insert({"insert into",
            TABLE_NAME,"(",INSERT_FIELDS,") ",
            "values(#{name},#{password},#{salt},#{headUrl})"})
    int addUser(User user);

    //select * from user where id = ?
    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where id = #{id}"})
    User selectById(int id);

    //select * from user where name = ?
    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME," where name = #{name}"})
    User selectByName(String name);

    //update user set password = ? where id = ?
    @Update({"update ",TABLE_NAME," set password = #{password} where id = #{id}"})
    void updatePassword(User user);

    //delete from user where id = 2
    @Delete({"delete from ",TABLE_NAME," where id = #{id}"})
    void deleteById(int id);
}
