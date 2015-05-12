package client.util

import com.amazonaws.AmazonWebServiceRequest
import com.amazonaws.handlers.AsyncHandler

import scala.concurrent.{Future, Promise}

class PromiseAsyncHandler[Request <: AmazonWebServiceRequest, Result](promise: Promise[Result]) extends AsyncHandler[Request, Result] {
  override def onError(exception: Exception): Unit = {
    promise.failure(exception)
  }

  override def onSuccess(request: Request, result: Result): Unit = {
    promise.success(result)
  }
  def future: Future[Result] = promise.future
}

object PromiseAsyncHandler {
  def apply[Request <: AmazonWebServiceRequest, Result](promise: Promise[Result]) = {
    new PromiseAsyncHandler[Request, Result](promise)
  }

  def apply[Request <: AmazonWebServiceRequest, Result]() = {
    new PromiseAsyncHandler[Request, Result](Promise[Result]())
  }

  def asyncToScalaFuture[Request <: AmazonWebServiceRequest, Result](func: AsyncHandler[Request, Result] => _) = {
    val asyncHandler = PromiseAsyncHandler[Request, Result]()
    func(asyncHandler)
    asyncHandler.future
  }
}