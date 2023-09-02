package raven.chat.swing;

import com.vdurmont.emoji.EmojiParser;
import java.awt.Font;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public interface ChatEvent {

    public void mousePressedSendButton(ActionEvent evt);

    public void mousePressedFileButton(ActionEvent evt);

    public void keyTyped(KeyEvent evt);
}
