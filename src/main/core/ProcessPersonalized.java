package core;

import core.stat.Statistics;
import core.stat.StatisticsPersonalized;
import logic.Ent;
import logic.Persistence;
import model.dao.DAOPgProcess;
import model.dao.DAOPgQuestion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

public class ProcessPersonalized implements Process {
    private final Persistence persistence;
    private final int userId;
    private final Iterator<Integer> it;

    static Logger log = LoggerFactory.getLogger(ProcessPersonalized.class);

    public ProcessPersonalized(Persistence persistence, int id) {
        this.persistence=persistence;
        this.userId = id;
        DAOPgQuestion dao = this.persistence.get(Ent.Question).dao();
        List<Integer> available = dao.availableByUser(id);
        log.info(available.toString());
        this.it = available.iterator();
    }

    @Override
    public Statistics statistics() {
        return new StatisticsPersonalized(this.userId);
    }

    @Override
    public void skip(int qid) {
        DAOPgProcess dao = persistence.get(Ent.Process).dao();
        dao.insert(new model.dto.Process(this.userId, qid, 0));
    }

    @Override
    public void store(int question, int answer) {
        DAOPgProcess dao = persistence.get(Ent.Process).dao();
        dao.insert(new model.dto.Process(this.userId, question, answer));
    }

    @Override
    public OneQuestion next() {
        return new OneQuestion(persistence, it.next());
    }

    @Override
    public boolean hasNext() {
        return it.hasNext();
    }
}
