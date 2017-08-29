package zhku.peishen.toutiao.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import zhku.peishen.toutiao.model.MailMessage;

/**
 * Created by ipc on 2017/8/6.
 */
@Mapper
public interface MailMessageDao {
    String TABLE_NAME = "mail_message";
    String INSERT_FILEDS = " username,password,salt";
    String SELECT_FILEDS = " id,"+INSERT_FILEDS;

    //insert into mail_message() values()
    @Insert({"insert into ",TABLE_NAME,"(",INSERT_FILEDS,") values(#{username},#{password},#{salt})"})
    void addMailMessage(@Param("username") String username,
                        @Param("password") String password,
                        @Param("salt") String salt);

    //select * from mail_message where id =
    @Select({"select ",SELECT_FILEDS," from ",TABLE_NAME," where id = #{id}"})
    MailMessage getMailMessageById(@Param("id") int id);
}
