package com.meetingsprod.meetings.main.view

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import com.google.firebase.FirebaseNetworkException
import com.meetingsprod.meetings.R
import com.meetingsprod.meetings.main.App.Companion.meetingsDao
import com.meetingsprod.meetings.main.api.MeetingsRepository
import com.meetingsprod.meetings.main.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.rvMeetings) }
    private val fabCreate by lazy { findViewById<FloatingActionButton>(R.id.fabCreate) }
    private val searchView by lazy { findViewById<SearchView>(R.id.search_view) }
    private val adapter = MeetingsAdapter {
        EditMeetingActivity.start(
            this,
            it
        )
    }
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(ItemOffsetDecoration())
        fabCreate.setOnClickListener {
            EditMeetingActivity.start(this, "-1")
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean = true

            override fun onQueryTextChange(query: String?): Boolean {
                disposable.clear()
                query?.let {
                    MeetingsRepository.search(it)
                        .subscribe({ adapter.data = it.toMutableList() }, this@MainActivity::showErrorToast)
                }
                return true
            }
        })
    }

    public override fun onStart() {
        super.onStart()
        disposable.add(
            meetingsDao.allFlowable().observeOn(AndroidSchedulers.mainThread())
                .map { it.filter { isToday(dateTimeFormat.parseOrNull(it.startDate) ?: Date(0)) } }
                .subscribe {
                    tvNoMeetings.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
                    adapter.data = it.toMutableList()
                }
        )
        requestMeetings()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.refresh -> {
            requestMeetings()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun requestMeetings() {
        disposable.add(MeetingsRepository.getMeetings().subscribe({ }, this::showErrorToast))
    }

    fun showErrorToast(throwable: Throwable) =
        Toast.makeText(
            this,
            when (throwable) {
                is FirebaseNetworkException -> getString(R.string.error_connection)
                else -> throwable.message
            },
            Toast.LENGTH_LONG
        )
            .show()

    override fun onStop() {
        super.onStop()
        disposable.clear()
    }

    override fun onBackPressed() {
        PreferenceManager.deleteUser()
        super.onBackPressed()
    }

    private fun isToday(date: Date): Boolean {
        val today = Calendar.getInstance()
        val other = Calendar.getInstance().apply { time = date }
        return other.get(Calendar.DATE) == today.get(Calendar.DATE)
    }
}
