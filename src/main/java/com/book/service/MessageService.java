package com.book.service;

import com.book.bean.Author;
import com.book.bean.Messages;
import com.book.bean.MessagesPlus;
import com.book.bean.Users;
import com.book.dao.AuthorDao;
import com.book.dao.MessageDao;
import com.book.dao.UserDao;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Myth on 2017/2/6 0006
 */
@Service
public class MessageService {
    @Autowired
    UserDao userDao;
    @Autowired
    AuthorDao authorDao;
    @Autowired
    MessageDao messageDao;
    private static org.slf4j.Logger Log = LoggerFactory.getLogger(MessageService.class);

    /**
     * 获取未读取的消息
     * @param id
     * @return List<MessagesPlus>
     */
    public List<MessagesPlus> getNowMessages(long id){
        List<Messages> list = null;
        List<MessagesPlus> lists = new ArrayList<MessagesPlus>();
        String receive_name="";
        String send_name="";
        try {
            list = messageDao.getAll("receive="+id+"");
            for(int i=0;i<list.size();i++){
                Messages m = list.get(i);
                //发送变接收，接收变发送
                long sendid = m.getSend();
                long receiveid = m.getReceive();

                receive_name = getName(sendid);
                send_name = getName(receiveid);

                Log.info("["+sendid+"]-["+receive_name+"]||["+receiveid+"]-["+send_name+"]");
                MessagesPlus p = new MessagesPlus(m,receive_name,send_name);
                lists.add(p);
            }} catch (Exception e) {
            e.printStackTrace();
        }
        return lists;
    }


    /**
     * 得到用户收到的所有消息
     *
     * @param id 收消息的人的id
     * @param readed 0：未读
     * @return Map<String,List<MessagesPlus>>
     */
    public Map<String,List<Messages>> getMessageList (long id, int readed){

        // 先分组得到String，然后再查询得到list
        Map<String, List<Messages>> result = new HashMap<String, List<Messages>>();
        try {
            List messages = messageDao.getAll("receive=" + id + " and readed=" + readed + " group by send");
            for (int i = 0; i < messages.size(); i++) {
                long temp_id = ((Messages) messages.get(i)).getSend();
                String send_name = getName(temp_id);
                List<Messages> messagesList = messageDao.getAll("receive=" + id + " and send=" + temp_id + " and readed=" + readed + " order by send_time");
                result.put(send_name, messagesList);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    //根据输入id得到用户名
    public String getName(long id)throws Exception{
        String temp = id+"";
        String name = "";
        if(temp.length()==12){
            Users u = (Users)userDao.getOne(id);
            if(u!=null ) name = u.getName();
        }else if(temp.length()==10){
            Author a = (Author)authorDao.getOne(id);
            if(a!=null) name = a.getName();
        }
        return name;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public AuthorDao getAuthorDao() {
        return authorDao;
    }

    public void setAuthorDao(AuthorDao authorDao) {
        this.authorDao = authorDao;
    }

    public MessageDao getMessageDao() {
        return messageDao;
    }

    public void setMessageDao(MessageDao messageDao) {
        this.messageDao = messageDao;
    }
}