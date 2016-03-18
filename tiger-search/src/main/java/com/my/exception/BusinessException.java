/**
 * 
 */
package com.my.exception;

/**
 * @author I311862
 *
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = -6370267525322739433L;

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public BusinessException(String errorCode) {
        super(errorCode);
    }

    public BusinessException(String errorCode, Throwable cause) {
        super(errorCode, cause);
    }

}
