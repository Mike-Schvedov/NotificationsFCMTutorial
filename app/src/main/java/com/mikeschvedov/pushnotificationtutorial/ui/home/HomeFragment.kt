package com.mikeschvedov.pushnotificationtutorial.ui.home

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.mikeschvedov.pushnotificationtutorial.databinding.FragmentHomeBinding
import com.mikeschvedov.pushnotificationtutorial.interfaces.TopicCallback
import com.mikeschvedov.pushnotificationtutorial.models.PushNotification
import com.mikeschvedov.pushnotificationtutorial.models.NotificationData
import com.mikeschvedov.pushnotificationtutorial.utils.extensions.getCurrentTopic
import com.mikeschvedov.pushnotificationtutorial.utils.extensions.setCurrentTopic

class HomeFragment : Fragment() {

    // Binding
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // ViewModel
    private lateinit var homeViewModel: HomeViewModel

    private val permissionManager = PermissionManager.from(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // ----------------------- ViewModel -------------------- //
        homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]
        // ----------------------- Binding ----------------------- //
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionManager
                .request(Permission.Notification)
                .rationale("We need permission to show Notifications")
                .checkPermission { granted: Boolean ->
                    if (granted) {
                        Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "No Permission to show notifications",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }


        // Subscribe to topic that is saved in SharedPref
        val topicOnStart = requireContext().getCurrentTopic()
        homeViewModel.subscribeToNewTopic(topicOnStart, object : TopicCallback {
            override fun onSubscribed() {
                // Setting the Text View
                binding.textviewCurrentTopic.text = topicOnStart
            }
        })

        onClickListeners()

        return binding.root
    }

    private fun onClickListeners() {
        binding.buttonSend.setOnClickListener {
            sendNewMessage()
        }

        binding.buttonChangeTopic.setOnClickListener {
            subscribeToNewTopic()
        }
    }

    private fun sendNewMessage() {

        val title = binding.titleEdittext.text.toString()
        val message = binding.messageEdittext.text.toString()
        val topic = binding.recipientTopicEdittext.text.toString()

        //TODO explain we don't actually send a notification, we send a message, that is displayed as a notification
        if (title.isNotEmpty() && message.isNotEmpty() && topic.isNotEmpty()) {
            PushNotification(
                NotificationData(title, message),
                "/topics/$topic"
            ).also {
                //Todo explain you can do foreach topic send notification
                homeViewModel.sendNewMessage(it)
            }
        } else {
            Toast.makeText(requireContext(), "MISSING INFORMATION", Toast.LENGTH_SHORT).show()
        }
    }

    private fun subscribeToNewTopic() {
        val topicInput = binding.newTopicEdittext.text.toString()
        if (topicInput.isNotEmpty()) {
            homeViewModel.subscribeToNewTopic(topicInput, object : TopicCallback {
                override fun onSubscribed() {
                    // Setting the Text View
                    binding.textviewCurrentTopic.text = topicInput
                    // Saving Topic in SharedPref
                    requireContext().setCurrentTopic(topicInput)
                }
            })
        } else {
            Toast.makeText(requireContext(), "NO TOPIC PROVIDED", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}