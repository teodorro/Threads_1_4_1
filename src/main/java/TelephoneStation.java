import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class TelephoneStation {
    public static final int CONSULTANTS_NUMBER = 3;
    public static final int CALLS_NUM = 10;
    public static final int CONSULTATION_TIME = 3000;
    public static final int CALLS_GAP = 1000;
    public static final int TIMEOUT = 4;

    private PriorityBlockingQueue<String> calls = new PriorityBlockingQueue<>();
    private ExecutorService es = Executors.newFixedThreadPool(CONSULTANTS_NUMBER + 1);

    public void start() {
        try {
            startCalls(CALLS_NUM);
            startAnswers(CONSULTANTS_NUMBER);
            es.awaitTermination(TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            es.shutdown();
        }
    }

    private void startAnswers(int consultantsNumber) {
        IntStream.range(0, consultantsNumber).forEach(x ->
                es.submit(() -> {
                    while (true) {
                        if (calls.size() > 0) {
                            String call = calls.poll();
                            sleep(CONSULTATION_TIME);
                            System.out.println(call + " finished");
                        } else{
                            sleep(CONSULTATION_TIME);
                            if (calls.size() == 0)
                                break;
                        }
                    }
                }));
    }

    private void startCalls(int callsNum) {
        es.submit(() -> {
            for (int i = 0; i < callsNum; i++) {
                calls.add("call " + i);
                System.out.println("call " + i + " added");
                sleep(CALLS_GAP);
            }
        });
    }

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
