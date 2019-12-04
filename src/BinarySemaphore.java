public class BinarySemaphore {
	
    boolean value;

    public BinarySemaphore() {
        this.value = true;
    }

    public BinarySemaphore(boolean initValue) {
        this.value = initValue;
    }

    public synchronized void P() {
        while (!this.value)
            try {
                wait();
            } catch (InterruptedException e) {
            }
        this.value = false;
    }

    public synchronized void V() {
        this.value = true;
        notify();
    }
}
