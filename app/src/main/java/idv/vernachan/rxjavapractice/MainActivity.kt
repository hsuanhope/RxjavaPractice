package idv.vernachan.rxjavapractice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.*
import io.reactivex.disposables.Disposable
import io.reactivex.observables.GroupedObservable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.SafeSubscriber
import io.reactivex.subscribers.SerializedSubscriber
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //subscribeOn - 指定observable應該在哪個scheduler上執行
        //Schedulers.io()-一種scheduler





    }

    //operator練習-filter和map
    //map - 映射，通過對序列的每一項都應用一個函數轉換成observable發射的數據，實質上對序列中的每一項數據執行一個函數，函數的參數就是這個數據項
    //filter - 過濾掉沒有通過謂詞測試的數據項，指發射通過測試的數據項
    fun operatorFilterMap(){
        Observable
            .just(1, 2, 3)
            .filter { it % 2 == 1 }
            .map { it * 2 }
            .subscribe(object : Observer<Int> {
                override fun onNext(i: Int) {
                    println(i)
                }

                override fun onComplete() {
                    println("Completed Observable.")
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                    println("Whoops: " + e.message)
                }
            })
    }

    //operator練習-sample
    //定期發射最新的數據
    fun operatorSample(){
        val timedObservable = Observable.interval(0, 1, TimeUnit.SECONDS)

        timedObservable
            .sample(3, TimeUnit.SECONDS)
            .subscribe(object : Observer<Long> {
                override fun onComplete() {
                    println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onNext(t: Long) {
                    println("onNext: $t")
                }

                override fun onError(e: Throwable) {
                }
            })
    }

    //operator練習-ignoreElements
    //丟掉所有資料後發射
    fun operatorIgnoreElements(){
        Observable.just(1,2,3,4,5,6,7,8)
            .ignoreElements()
            .subscribe(object : CompletableObserver {
                override fun onComplete() {

                    println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribed")
                }

                override fun onError(e: Throwable) {

                }
            })
    }

    //operator練習-elementAt
    //指發射第N項數據
    fun operatorElementAt(){
        Observable.just(1, 2, 3, 4, 5, 6)
            .elementAt(1)
            .subscribe(object : MaybeObserver<Int> {
                override fun onSuccess(t: Int) {
                    println("onSuccess: $t")
                }

                override fun onComplete() {
                    println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onError(e: Throwable) {
                }
            })
    }

    //operator練習-distinct
    //過濾掉重複的資料
    fun operatorDistinct(){
        Observable.just(10, 20, 20, 10, 30, 40, 70, 60, 70)
            .distinct()
            .subscribe(object : Observer<Int> {
                override fun onComplete() {
                    println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onNext(t: Int) {
                    println("onNext: $t")
                }

                override fun onError(e: Throwable) {
                }
            })
    }

    //operator練習-scan
    //對observable發射的每一項數據應用一個函數，然後按順序依次發射這些值
    fun operatorScan(){
        Observable.just("J","A","V","A")
            .scan { t1, t2 -> t1 + t2}
            .subscribe(object : Observer<String> {
                override fun onComplete() {
                    println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onNext(t: String) {
                    println("onNext: $t")
                }

                override fun onError(e: Throwable) {

                }
            })
    }

    //operator練習-groupBy
    //分組，將原來的obervable分拆為observable集合，將原始observable發射的數據按key分組，每一個observable發射一組不同的數據
    fun operatorGroupBy(){

        val EVEN_NUMBER_KEY = "even number"
        val ODD_NUMBER_KEY = "odd number"

        Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9)
            .groupBy { t ->
                if (t % 2 == 0) {
                    EVEN_NUMBER_KEY
                } else {
                    ODD_NUMBER_KEY
                }
            }
            .subscribe(object : Observer<GroupedObservable<String, Int>> {
                override fun onComplete() {
                    println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onNext(groupedObservable: GroupedObservable<String, Int>) {
                    if (groupedObservable.key == EVEN_NUMBER_KEY) {
                        groupedObservable.subscribe(object : Observer<Int> {
                            override fun onComplete() {
                                println("Group onComplete")
                            }

                            override fun onSubscribe(d: Disposable) {
                                println("Group onSubscribe")
                            }

                            override fun onNext(t: Int) {
                                println("Group onNext : $t")
                            }

                            override fun onError(e: Throwable) {
                            }

                        })
                    }
                }

                override fun onError(e: Throwable) {
                }
            })
    }

    //operator練習-concatMap
    //連接對應，將資料項經由一個函數後變成多個observable，再發射這些observable
    fun operatorConcatMap(){
        Observable.just(1,2,3,4,5,6)
            .concatMap { t -> getModifiedObservable(t) }
            .subscribeOn(Schedulers.io())
            .subscribe(object : Observer<Int> {
                override fun onComplete() {
                    println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onNext(t: Int) {
                    println("onNext: $t")
                }

                override fun onError(e: Throwable) {
                }
            })
    }

    fun getModifiedObservable(integer: Int): Observable<Int> {
        return Observable.create(object : ObservableOnSubscribe<Int> {
            override fun subscribe(emitter: ObservableEmitter<Int>) {
                emitter.onNext(integer * 2)
                emitter.onComplete()
            }
        })
            .subscribeOn(Schedulers.io())
    }
}