package su.svn.hiload.socialnetwork.services;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import su.svn.hiload.socialnetwork.dao.MessageDao;
import su.svn.hiload.socialnetwork.dao.UserProfileDao;
import su.svn.hiload.socialnetwork.model.Message;
import su.svn.hiload.socialnetwork.model.security.UserProfile;

@Service
public class ReactiveService {

    private final UserProfileDao userProfileDao;

    private final MessageDao messageDao;

    public ReactiveService(UserProfileDao userProfileDao, MessageDao messageDao) {
        this.userProfileDao = userProfileDao;
        this.messageDao = messageDao;
    }

    public Flux<UserProfile> searchUserProfilesAll() {
        return userProfileDao.findAll();
    }

    public Flux<Message> searchMessagesAll() {
        return messageDao.findAll();
    }

    public Mono<Message> create(Message message) {
        return messageDao.create(message).map(i -> message);
    }

    public Mono<Message> update(Message message) {
        return messageDao.save(message);
    }

    public Mono<Void> delete(Message message) {
        return messageDao.delete(message);
    }
}
