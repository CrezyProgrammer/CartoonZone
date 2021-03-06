package com.app.cartoonvideos.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.cartoonvideos.MyApp
import com.app.cartoonvideos.adapter.ItemAdapter
import com.app.cartoonvideos.databinding.FragmentHomeBinding
import com.app.cartoonvideos.entity.Video
import com.app.cartoonvideos.repo.DataRepository
import com.app.cartoonvideos.viewModel.MainViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment(private  val s: String) : Fragment() {
    var item: ArrayList<Video>? = null
    @Inject
    lateinit var myRepository: DataRepository
    private lateinit var binding: FragmentHomeBinding
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =  FragmentHomeBinding.inflate(layoutInflater)
        binding.progressBar.visibility=View.GONE
        val root = binding.root
        item = ArrayList()

        binding.recyclerview.layoutManager = LinearLayoutManager(activity)
        binding.recyclerview.adapter = ItemAdapter(item!!)

MyApp.category=s

        activity?.let { it ->
            mainViewModel.pigeonListLiveData.observe(it,{
                binding.recyclerview.adapter = ItemAdapter(it as ArrayList<Video>)
                showRecycler()
            })
        }








        return root
    }

    private fun showRecycler() {
        Handler(Looper.getMainLooper()).post {

        }


        val ref = FirebaseDatabase.getInstance().getReference(s)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
               //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()) {
                    item?.clear()
                    for (v in p0.children) {
Log.i("123321","80:$p0")
                        val bal = Video(id = v.child("id").value.toString(),
                            title = v.child("title").value.toString(),
                            duration = v.child("duration").value.toString(),
                         category =  s,
                           views =  if(v.child("views").exists())v.child("views").value.toString() else "0k ",
                           date =  if(v.child("date").exists())v.child("date").value.toString() else "0 month "

                        )
                        item?.add(bal)
                        lifecycleScope.launch {
                            myRepository.insert(bal)
                        }
                    }
                    binding.progressBar.visibility = View.GONE
                    binding.recyclerview.adapter = ItemAdapter(item!!)

                }

            }
        })

    }
}