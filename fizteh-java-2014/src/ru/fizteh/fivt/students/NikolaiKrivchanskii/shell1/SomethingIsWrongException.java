package ru.fizteh.fivt.students.NikolaiKrivchanskii.shell1;


public final class SomethingIsWrongException extends Exception {
    public SomethingIsWrongException() { 
        super(); 
    }
    
    public SomethingIsWrongException(String message) { 
        super(message); 
    }
    
    public SomethingIsWrongException(String message, Throwable cause) { 
        super(message, cause); 
    }
    
    public SomethingIsWrongException(Throwable cause) { 
        super(cause); 
    }
}
