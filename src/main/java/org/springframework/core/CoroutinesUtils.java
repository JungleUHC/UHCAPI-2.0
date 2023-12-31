/*    */ package org.springframework.core;
/*    */ 
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.Objects;
/*    */ import kotlin.Unit;
/*    */ import kotlin.coroutines.Continuation;
/*    */ import kotlin.coroutines.CoroutineContext;
/*    */ import kotlin.jvm.JvmClassMappingKt;
/*    */ import kotlin.reflect.KCallable;
/*    */ import kotlin.reflect.KClassifier;
/*    */ import kotlin.reflect.KFunction;
/*    */ import kotlin.reflect.full.KCallables;
/*    */ import kotlin.reflect.jvm.KCallablesJvm;
/*    */ import kotlin.reflect.jvm.ReflectJvmMapping;
/*    */ import kotlinx.coroutines.BuildersKt;
/*    */ import kotlinx.coroutines.CoroutineScope;
/*    */ import kotlinx.coroutines.CoroutineStart;
/*    */ import kotlinx.coroutines.Deferred;
/*    */ import kotlinx.coroutines.Dispatchers;
/*    */ import kotlinx.coroutines.GlobalScope;
/*    */ import kotlinx.coroutines.flow.Flow;
/*    */ import kotlinx.coroutines.reactor.MonoKt;
/*    */ import kotlinx.coroutines.reactor.ReactorFlowKt;
/*    */ import org.reactivestreams.Publisher;
/*    */ import reactor.core.publisher.Flux;
/*    */ import reactor.core.publisher.Mono;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class CoroutinesUtils
/*    */ {
/*    */   public static <T> Mono<T> deferredToMono(Deferred<T> source) {
/* 55 */     return MonoKt.mono((CoroutineContext)Dispatchers.getUnconfined(), (scope, continuation) -> source.await(continuation));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static <T> Deferred<T> monoToDeferred(Mono<T> source) {
/* 63 */     return BuildersKt.async((CoroutineScope)GlobalScope.INSTANCE, (CoroutineContext)Dispatchers.getUnconfined(), CoroutineStart.DEFAULT, (scope, continuation) -> MonoKt.awaitSingleOrNull(source, continuation));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Publisher<?> invokeSuspendingFunction(Method method, Object target, Object... args) {
/* 73 */     KFunction<?> function = Objects.<KFunction>requireNonNull(ReflectJvmMapping.getKotlinFunction(method));
/* 74 */     if (method.isAccessible() && !KCallablesJvm.isAccessible((KCallable)function)) {
/* 75 */       KCallablesJvm.setAccessible((KCallable)function, true);
/*    */     }
/* 77 */     KClassifier classifier = function.getReturnType().getClassifier();
/*    */ 
/*    */ 
/*    */     
/* 81 */     Mono<Object> mono = MonoKt.mono((CoroutineContext)Dispatchers.getUnconfined(), (scope, continuation) -> KCallables.callSuspend((KCallable)function, getSuspendedFunctionArgs(target, args), continuation)).filter(result -> !Objects.equals(result, Unit.INSTANCE)).onErrorMap(InvocationTargetException.class, InvocationTargetException::getTargetException);
/* 82 */     if (classifier != null && classifier.equals(JvmClassMappingKt.getKotlinClass(Flow.class))) {
/* 83 */       return (Publisher<?>)mono.flatMapMany(CoroutinesUtils::asFlux);
/*    */     }
/* 85 */     return (Publisher<?>)mono;
/*    */   }
/*    */   
/*    */   private static Object[] getSuspendedFunctionArgs(Object target, Object... args) {
/* 89 */     Object[] functionArgs = new Object[args.length];
/* 90 */     functionArgs[0] = target;
/* 91 */     System.arraycopy(args, 0, functionArgs, 1, args.length - 1);
/* 92 */     return functionArgs;
/*    */   }
/*    */   
/*    */   private static Flux<?> asFlux(Object flow) {
/* 96 */     return ReactorFlowKt.asFlux((Flow)flow);
/*    */   }
/*    */ }


/* Location:              /home/altaks/Téléchargements/mumblelink-1.0-SNAPSHOT.jar!/org/springframework/core/CoroutinesUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */