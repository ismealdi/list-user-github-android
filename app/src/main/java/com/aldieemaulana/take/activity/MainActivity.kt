package com.aldieemaulana.take.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.widget.LinearLayout
import com.aldieemaulana.take.R
import com.aldieemaulana.take.activity.base.BaseActivity
import com.aldieemaulana.take.adapter.UserListAdapter
import com.aldieemaulana.take.listener.AmScrollListener
import com.aldieemaulana.take.api.User as Api
import com.aldieemaulana.take.logger.AlLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.aldieemaulana.take.model.user.User
import kotlinx.android.synthetic.main.activity_main.*
import com.aldieemaulana.take.view.AmEditText
import android.os.Handler
import android.view.View

/**
 * Created by Al on 26/06/18 for Cermati
 */

class MainActivity : BaseActivity() {

    private val user by lazy { Api.create() }
    private val delay: Long = 1500
    private val perPage : Int = 10
    private var adapter: UserListAdapter? = null
    private var totalPage : Int = 0
    private var currentPage : Int = 1
    private var nextPage : Boolean = false
    private var isSearch : Boolean = false
    private var queryCurrent : String = ""
    private var lastText: Long = 0
    private var handler = Handler()

    private var userLinearLayout = LinearLayoutManager(context, LinearLayout.VERTICAL, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setInit()
    }

    private fun setInit() {
        recyclerView.layoutManager = userLinearLayout
        setListener()
    }

    private fun setListener() {
        recyclerView.addOnScrollListener(object : AmScrollListener(userLinearLayout) {
            override fun onLoadMore(current_page: Int) {
                if(nextPage) {
                    currentPage++
                    doSearch(queryCurrent, currentPage, perPage)
                }
            }

        })

        textSearch.onChange()
    }

    private fun doInitList(data: List<User>, q: String) {
        errorLayout.visibility = View.GONE

        if(adapter == null) {
            adapter = UserListAdapter(data.toMutableList())
            recyclerView.adapter = adapter
        }else{
            if(queryCurrent != q || (data.count() == 1 && currentPage == 1))
                adapter!!.clear()

            adapter!!.addAll(data)
            adapter!!.notifyItemRangeChanged(0, adapter!!.itemCount)
        }

    }

    private fun doClearList() {
        if(adapter != null) {
            queryCurrent = ""
            totalPage = 0
            currentPage = 1
            adapter!!.clear()
            adapter!!.notifyItemRangeChanged(0, 0)
        }
    }

    private fun doSearch(q: String, p: Int, pP: Int) {
        textSearch.isEnabled = true
        if (queryCurrent != q)
            doClearList()

        if(!isSearch) {
            loading(true)
            AlLog.e("Query ${q}, ${p}")
            disposable = user.search(q, p, pP)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { result ->
                                loading(false)
                                val count = result.items.count()
                                if(count > 0){
                                    totalPage += count
                                    doInitList(result.items, q)
                                    nextPage = (result.totalCount!! > totalPage && result.totalCount > perPage)
                                }else{
                                    doClearList()
                                    doInitNotFound(getString(R.string.text_404), getString(R.string.text_data_is_not_found))
                                }
                            },
                            { error ->
                                loading(false)
                                AlLog.e("${error.message}")
                                doClearList()
                                doInitNotFound(getString(R.string.text_403), getString(R.string.text_api_rate_is_limit))

                                Handler().postDelayed(
                                        { doSearch(textSearch.text.toString(), currentPage, perPage) },
                                        60000)

                                doStartTick()

                            }
                    )

            queryCurrent = q
        }


    }

    private fun doStartTick() {
        textSearch.isEnabled = false
        Handler().postDelayed({
            val count = Integer.parseInt(textCode.text.toString()) - 1
            textCode.text = count.toString()
            if(count > 0)
                doStartTick()

        },1000)
    }

    private fun loading(b: Boolean) {
        errorLayout.visibility = View.GONE
        isSearch = b

        if(b)
            loadingLayout.visibility = View.VISIBLE
        else
            loadingLayout.visibility = View.GONE


    }

    private fun doInitNotFound(c: String, m: String) {
        errorLayout.visibility = View.VISIBLE
        textCode.text = c
        textDescription.text = m
    }

    private val handlerSearch = Runnable {
        if (System.currentTimeMillis() > lastText + delay - 500 && textSearch.text.toString().isNotEmpty()) {
            doSearch(textSearch.text.toString(), currentPage, perPage)
        }
    }

    private fun AmEditText.onChange() {
        this.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isEmpty())
                    doClearList()
                else{
                   if(adapter != null) {
                       adapter?.filter?.filter(textSearch.text.toString())
                   }
                }
            }

            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    if(adapter == null || adapter?.itemCount == 0) {
                        lastText = System.currentTimeMillis()
                        handler.postDelayed(handlerSearch, delay)
                    }
                } else {
                    doClearList()
                }
            }
        })
    }

}
