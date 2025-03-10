/*
 * Copyright (c) 2008, 2024, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package sun.awt;

import javax.accessibility.AccessibleContext;
import java.awt.*;
import java.awt.color.ICC_Profile;
import java.awt.event.FocusEvent.Cause;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.dnd.peer.DropTargetContextPeer;
import java.awt.event.InputEvent;
import java.awt.event.InvocationEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;
import java.awt.peer.ComponentPeer;

import java.awt.peer.MenuComponentPeer;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;

import java.io.File;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.function.BooleanSupplier;
import javax.accessibility.AccessibleBundle;

import sun.java2d.cmm.Profile;

/**
 * The AWTAccessor utility class.
 * The main purpose of this class is to enable accessing
 * private and package-private fields of classes from
 * different classes/packages. See sun.misc.SharedSecretes
 * for another example.
 */
public final class AWTAccessor {

    /*
     * We don't need any objects of this class.
     * It's rather a collection of static methods
     * and interfaces.
     */
    private AWTAccessor() {
    }

    /*
     * An interface of accessor for the java.awt.color.ICC_Profile class.
     */
    public interface ICC_ProfileAccessor {
        /*
         * Activates and returns the deferred standard profiles.
         */
        Profile cmmProfile(ICC_Profile profile);
    }

    /*
     * An interface of accessor for the java.awt.Component class.
     */
    public interface ComponentAccessor {
        /*
         * Sets whether the native background erase for a component
         * has been disabled via SunToolkit.disableBackgroundErase().
         */
        void setBackgroundEraseDisabled(Component comp, boolean disabled);
        /*
         * Indicates whether the native background erase for a
         * component has been disabled via
         * SunToolkit.disableBackgroundErase().
         */
        boolean getBackgroundEraseDisabled(Component comp);
        /*
         *
         * Gets the bounds of this component in the form of a
         * {@code Rectangle} object. The bounds specify this
         * component's width, height, and location relative to
         * its parent.
         */
        Rectangle getBounds(Component comp);

        /**
         * Sets GraphicsConfiguration value for the component.
         */
        void setGraphicsConfiguration(Component comp, GraphicsConfiguration gc);
        /*
         * Requests focus to the component.
         */
        void requestFocus(Component comp, Cause cause);
        /*
         * Determines if the component can gain focus.
         */
        boolean canBeFocusOwner(Component comp);

        /**
         * Returns whether the component is visible without invoking
         * any client code.
         */
        boolean isVisible(Component comp);

        /**
         * Sets the RequestFocusController.
         */
        void setRequestFocusController(RequestFocusController requestController);

        /**
         * Returns the appContext of the component.
         */
        AppContext getAppContext(Component comp);

        /**
         * Sets the appContext of the component.
         */
        void setAppContext(Component comp, AppContext appContext);

        /**
         * Returns the parent of the component.
         */
        Container getParent(Component comp);

        /**
         * Sets the parent of the component to the specified parent.
         */
        void setParent(Component comp, Container parent);

        /**
         * Resizes the component to the specified width and height.
         */
        void setSize(Component comp, int width, int height);

        /**
         * Returns the location of the component.
         */
        Point getLocation(Component comp);

        /**
         * Moves the component to the new location.
         */
        void setLocation(Component comp, int x, int y);

        /**
         * Determines whether this component is enabled.
         */
        boolean isEnabled(Component comp);

        /**
         * Determines whether this component is displayable.
         */
        boolean isDisplayable(Component comp);

        /**
         * Gets the cursor set in the component.
         */
        Cursor getCursor(Component comp);

        /**
         * Returns the peer of the component.
         */
        <T extends ComponentPeer> T getPeer(Component comp);

        /**
         * Sets the peer of the component to the specified peer.
         */
        void setPeer(Component comp, ComponentPeer peer);

        /**
         * Determines whether this component is lightweight.
         */
        boolean isLightweight(Component comp);

        /**
         * Returns whether or not paint messages received from
         * the operating system should be ignored.
         */
        boolean getIgnoreRepaint(Component comp);

        /**
         * Returns the width of the component.
         */
        int getWidth(Component comp);

        /**
         * Returns the height of the component.
         */
        int getHeight(Component comp);

        /**
         * Returns the x coordinate of the component.
         */
        int getX(Component comp);

        /**
         * Returns the y coordinate of the component.
         */
        int getY(Component comp);

        /**
         * Gets the foreground color of this component.
         */
        Color getForeground(Component comp);

        /**
         * Gets the background color of this component.
         */
        Color getBackground(Component comp);

        /**
         * Sets the background of this component to the specified color.
         */
        void setBackground(Component comp, Color background);

        /**
         * Gets the font of the component.
         */
        Font getFont(Component comp);

        /**
         * Processes events occurring on this component.
         */
        void processEvent(Component comp, AWTEvent e);

        /**
         * Revalidates the component synchronously.
         */
        void revalidateSynchronously(Component comp);

        /**
         * Creates a new strategy for multi-buffering on this component.
         */
        void createBufferStrategy(Component comp, int numBuffers,
                BufferCapabilities caps) throws AWTException;

        /**
         * returns the buffer strategy used by this component.
         */
        BufferStrategy getBufferStrategy(Component comp);
    }

    /*
     * An interface of accessor for the java.awt.Container class.
     */
    public interface ContainerAccessor {
        /**
         * Validates the container unconditionally.
         */
        void validateUnconditionally(Container cont);

        /**
         *
         * Access to the private version of findComponentAt method which has
         * a controllable behavior. Setting 'ignoreEnabled' to 'false'
         * bypasses disabled Components during the search.
         */
        Component findComponentAt(Container cont, int x, int y, boolean ignoreEnabled);

        /**
         * Starts LW Modal.
         */
        void startLWModal(Container cont);

        /**
         * Starts LW Modal.
         */
        void stopLWModal(Container cont);
    }

    /*
     * An interface of accessor for java.awt.Window class.
     */
    public interface WindowAccessor {
        /*
         * Update the image of a non-opaque (translucent) window.
         */
        void updateWindow(Window window);

        void setPopupParent(Window window, Component component);
        Component getPopupParent(Window window);

        /** Sets the synchronous status of focus requests on lightweight
         * components in the specified window to the specified value.
         */
        void setLWRequestStatus(Window changed, boolean status);

        /**
         * Indicates whether this window should receive focus on subsequently
         * being shown, or being moved to the front.
         */
        boolean isAutoRequestFocus(Window w);

        /**
         * Indicates whether the specified window is an utility window for TrayIcon.
         */
        boolean isTrayIconWindow(Window w);

        /**
         * Marks the specified window as an utility window for TrayIcon.
         */
        void setTrayIconWindow(Window w, boolean isTrayIconWindow);

        /**
         * Return an array containing all the windows this
         * window currently owns.
         */
        Window[] getOwnedWindows(Window w);

        boolean countersEnabled(Window w);
        void bumpCounter(Window w, String counterName);
        long getCounter(Window w, String counterName);
        long getCounterPerSecond(Window w, String counterName);
    }

    /**
     * An accessor for the AWTEvent class.
     */
    public interface AWTEventAccessor {
        /**
         * Marks the event as posted.
         */
        void setPosted(AWTEvent ev);

        /**
         * Sets the flag on this AWTEvent indicating that it was
         * generated by the system.
         */
        void setSystemGenerated(AWTEvent ev);

        /**
         * Indicates whether this AWTEvent was generated by the system.
         */
        boolean isSystemGenerated(AWTEvent ev);

        /**
         * Returns binary data associated with this event;
         */
        byte[] getBData(AWTEvent ev);

        /**
         * Associates binary data with this event;
         */
        void setBData(AWTEvent ev, byte[] bdata);
    }

    public interface InputEventAccessor {
        /*
         * Accessor for InputEvent.getButtonDownMasks()
         */
        int[] getButtonDownMasks();

        /*
         * Accessor for InputEvent.canAccessSystemClipboard field
         */
        boolean canAccessSystemClipboard(InputEvent event);
        void setCanAccessSystemClipboard(InputEvent event,
                boolean canAccessSystemClipboard);
    }

    /**
     * An accessor for the MouseEvent class.
     */
    public interface MouseEventAccessor {
        /**
         * Indicates whether the event is a result of a touch event.
         */
        boolean isCausedByTouchEvent(MouseEvent ev);

        /**
         * Sets whether the event is a result of a touch event.
         */
        void setCausedByTouchEvent(MouseEvent ev, boolean causedByTouchEvent);
    }

    /*
     * An accessor for the java.awt.Frame class.
     */
    public interface FrameAccessor {
        /*
         * Sets the state of this frame.
         */
        void setExtendedState(Frame frame, int state);
        /*
         * Gets the state of this frame.
         */
       int getExtendedState(Frame frame);
        /*
         * Gets the maximized bounds of this frame.
         */
       Rectangle getMaximizedBounds(Frame frame);
    }

    /**
     * An interface of accessor for the java.awt.KeyboardFocusManager class.
     */
    public interface KeyboardFocusManagerAccessor {
        /**
         * Indicates whether the native implementation should
         * proceed with a pending focus request for the heavyweight.
         */
        int shouldNativelyFocusHeavyweight(Component heavyweight,
                                           Component descendant,
                                           boolean temporary,
                                           boolean focusedWindowChangeAllowed,
                                           long time,
                                           Cause cause,
                                           boolean highPriorityEvents);
        /**
         * Delivers focus for the lightweight descendant of the heavyweight
         * synchronously.
         */
        boolean processSynchronousLightweightTransfer(Component heavyweight,
                                                      Component descendant,
                                                      boolean temporary,
                                                      boolean focusedWindowChangeAllowed,
                                                      long time);
        /**
         * Removes the last focus request for the heavyweight from the queue.
         */
        void removeLastFocusRequest(Component heavyweight);

        /**
         * Gets the most recent focus owner in the window.
         */
        Component getMostRecentFocusOwner(Window window);

        /**
         * Sets the most recent focus owner in the window.
         */
        void setMostRecentFocusOwner(Window window, Component component);

        /**
         * Returns current KFM of the specified AppContext.
         */
        KeyboardFocusManager getCurrentKeyboardFocusManager(AppContext ctx);

        /**
         * Return the current focus cycle root
         */
        Container getCurrentFocusCycleRoot();
    }

    /**
     * An accessor for the MenuComponent class.
     */
    public interface MenuComponentAccessor {
        /**
         * Returns the appContext of the menu component.
         */
        AppContext getAppContext(MenuComponent menuComp);

        /**
         * Sets the appContext of the menu component.
         */
        void setAppContext(MenuComponent menuComp, AppContext appContext);

        /**
         * Returns the peer of the menu component.
         */
        <T extends MenuComponentPeer> T getPeer(MenuComponent menuComp);

        /**
         * Returns the menu container of the menu component.
         */
        MenuContainer getParent(MenuComponent menuComp);

        /**
         * Sets the menu container of the menu component.
         */
        void  setParent(MenuComponent menuComp, MenuContainer menuContainer);

        /**
         * Gets the font used for this menu component.
         */
        Font getFont_NoClientCode(MenuComponent menuComp);
    }

    /**
     * An accessor for the EventQueue class
     */
    public interface EventQueueAccessor {
        /**
         * Gets the event dispatch thread.
         */
        Thread getDispatchThread(EventQueue eventQueue);

        /**
         * Checks if the current thread is EDT for the given EQ.
         */
        public boolean isDispatchThreadImpl(EventQueue eventQueue);

        /**
         * Removes any pending events for the specified source object.
         */
        void removeSourceEvents(EventQueue eventQueue, Object source, boolean removeAllEvents);

        /**
         * Returns whether an event is pending on any of the separate Queues.
         */
        boolean noEvents(EventQueue eventQueue);

        /**
         * Called from PostEventQueue.postEvent to notify that a new event
         * appeared.
         */
        void wakeup(EventQueue eventQueue, boolean isShutdown);

        /**
         * Static in EventQueue
         */
        void invokeAndWait(Object source, Runnable r)
            throws InterruptedException, InvocationTargetException;

        /**
         * Sets the delegate for the EventQueue used by FX/AWT single threaded mode
         */
        public void setFwDispatcher(EventQueue eventQueue, FwDispatcher dispatcher);

        /**
         * Gets most recent event time in the EventQueue
         */
        long getMostRecentEventTime(EventQueue eventQueue);

        /**
         * Creates a secondary loop with the provided condition.
         */
        SecondaryLoop createSecondaryLoop(EventQueue eventQueue, BooleanSupplier cond);
    }

    /*
     * An accessor for the PopupMenu class
     */
    public interface PopupMenuAccessor {
        /*
         * Returns whether the popup menu is attached to a tray
         */
        boolean isTrayIconPopup(PopupMenu popupMenu);
    }

    /*
     * An accessor for the FileDialog class
     */
    public interface FileDialogAccessor {
        /*
         * Sets the files the user selects
         */
        void setFiles(FileDialog fileDialog, File[] files);

        /*
         * Sets the file the user selects
         */
        void setFile(FileDialog fileDialog, String file);

        /*
         * Sets the directory the user selects
         */
        void setDirectory(FileDialog fileDialog, String directory);

        /*
         * Returns whether the file dialog allows the multiple file selection.
         */
        boolean isMultipleMode(FileDialog fileDialog);
    }

    /*
     * An accessor for the ScrollPaneAdjustable class.
     */
    public interface ScrollPaneAdjustableAccessor {
        /*
         * Sets the value of this scrollbar to the specified value.
         */
        void setTypedValue(final ScrollPaneAdjustable adj, final int v,
                           final int type);
    }

    /**
     * An accessor for the CheckboxMenuItem class
     */
    public interface CheckboxMenuItemAccessor {
        /**
         * Returns whether menu item is checked
         */
        boolean getState(CheckboxMenuItem cmi);
    }

    /**
     * An accessor for the Cursor class
     */
    public interface CursorAccessor {
        /**
         * Returns pData of the Cursor class according to scale or 0 otherwise
         */
        long getPData(Cursor cursor, int scale);

        /**
         * Returns pData of the Cursor class
         */
        long getPData(Cursor cursor);

        /**
         * Sets pData to the Cursor class according to scale
         */
        void setPData(Cursor cursor, int scale, long pData);

        /**
         * Sets pData to the Cursor class
         */
        void setPData(Cursor cursor, long pData);

        /**
         * Return type of the Cursor class
         */
        int getType(Cursor cursor);
    }

    /**
     * An accessor for the MenuBar class
     */
    public interface MenuBarAccessor {
        /**
         * Returns help menu
         */
        Menu getHelpMenu(MenuBar menuBar);

        /**
         * Returns menus
         */
        Vector<Menu> getMenus(MenuBar menuBar);
    }

    /**
     * An accessor for the MenuItem class
     */
    public interface MenuItemAccessor {
        /**
         * Returns whether menu item is enabled
         */
        boolean isEnabled(MenuItem item);

        /**
         * Gets the command name of the action event that is fired
         * by this menu item.
         */
        String getActionCommandImpl(MenuItem item);

        /**
         * Returns true if the item and all its ancestors are
         * enabled, false otherwise
         */
        boolean isItemEnabled(MenuItem item);

        /**
         * Returns label
         */
        String getLabel(MenuItem item);

        /**
         * Returns shortcut
         */
        MenuShortcut getShortcut(MenuItem item);
    }

    /**
     * An accessor for the Menu class
     */
    public interface MenuAccessor {
        /**
         * Returns vector of the items that are part of the Menu
         */
        Vector<MenuItem> getItems(Menu menu);
    }

    /**
     * An accessor for the KeyEvent class
     */
    public interface KeyEventAccessor {
        /**
         * Sets rawCode field for KeyEvent
         */
        void setRawCode(KeyEvent ev, long rawCode);

        /**
         * Sets primaryLevelUnicode field for KeyEvent
         */
        void setPrimaryLevelUnicode(KeyEvent ev, long primaryLevelUnicode);

        /**
         * Sets extendedKeyCode field for KeyEvent
         */
        void setExtendedKeyCode(KeyEvent ev, long extendedKeyCode);

        /**
         * Gets original source for KeyEvent
         */
        Component getOriginalSource(KeyEvent ev);

        /**
         * Gets isProxyActive field for KeyEvent
         */
        boolean isProxyActive(KeyEvent ev);

        /**
         * Returns the JBR-specific property map
         */
        Map<String, Object> getJBRExtraProperties(KeyEvent ev);

        /**
         * Overwrites the JBR-specific property map
         */
        void setJBRExtraProperties(KeyEvent ev, Map<String, Object> properties);
    }

    /**
     * An accessor for the ClientPropertyKey class
     */
    public interface ClientPropertyKeyAccessor {
        /**
         * Retrieves JComponent_TRANSFER_HANDLER enum object
         */
        Object getJComponent_TRANSFER_HANDLER();
    }

    /**
     * An accessor for the SystemTray class
     */
    public interface SystemTrayAccessor {
        /**
         * Support for reporting bound property changes for Object properties.
         */
        void firePropertyChange(SystemTray tray, String propertyName, Object oldValue, Object newValue);
    }

    /**
     * An accessor for the TrayIcon class
     */
    public interface TrayIconAccessor {
        void addNotify(TrayIcon trayIcon) throws AWTException;
        void removeNotify(TrayIcon trayIcon);
    }

    /**
     * An accessor for the DefaultKeyboardFocusManager class
     */
    public interface DefaultKeyboardFocusManagerAccessor {
        public void consumeNextKeyTyped(DefaultKeyboardFocusManager dkfm, KeyEvent e);
    }

    /*
     * An accessor for the SequencedEventAccessor class
     */
    public interface SequencedEventAccessor {
        /*
         * Returns the nested event.
         */
        AWTEvent getNested(AWTEvent sequencedEvent);

        /*
         * Returns true if the event is an instances of SequencedEvent.
         */
        boolean isSequencedEvent(AWTEvent event);

        /*
         * Creates SequencedEvent with the given nested event
         */
        AWTEvent create(AWTEvent event);
    }

    /*
     * An accessor for the Toolkit class
     */
    public interface ToolkitAccessor {
        void setPlatformResources(ResourceBundle bundle);
        void setDesktopProperty(Toolkit tk, String prop, Object value);
    }

    /*
     * An accessor object for the InvocationEvent class
     */
    public interface InvocationEventAccessor {
        void dispose(InvocationEvent event);
    }

    /*
     * An accessor object for the SystemColor class
     */
    public interface SystemColorAccessor {
        void updateSystemColors();
    }

    /*
     * An accessor object for the AccessibleContext class
     */
    public interface AccessibleContextAccessor {
        void setAppContext(AccessibleContext accessibleContext, AppContext appContext);
        AppContext getAppContext(AccessibleContext accessibleContext);
        Object getNativeAXResource(AccessibleContext accessibleContext);
        void setNativeAXResource(AccessibleContext accessibleContext, Object value);
    }

    /*
     * An accessor object for the AccessibleContext class
     */
    public interface AccessibleBundleAccessor {
        String getKey(AccessibleBundle accessibleBundle);
    }

    /*
     * An accessor object for the DragSourceContext class
     */
    public interface DragSourceContextAccessor {
        /**
         * Returns the peer of the DragSourceContext.
         */
        DragSourceContextPeer getPeer(DragSourceContext dsc);
    }

    /*
     * An accessor object for the DropTargetContext class
     */
    public interface DropTargetContextAccessor {
        /**
         * Resets the DropTargetContext.
         */
        void reset(DropTargetContext dtc);
        /**
         * Sets the {@code DropTargetContextPeer}
         */
        void setDropTargetContextPeer(DropTargetContext dtc,
                                      DropTargetContextPeer dtcp);
    }

    /*
     * Accessor instances are initialized in the static initializers of
     * corresponding AWT classes by using setters defined below.
     */
    private static ICC_ProfileAccessor iccProfileAccessor;
    private static ComponentAccessor componentAccessor;
    private static ContainerAccessor containerAccessor;
    private static WindowAccessor windowAccessor;
    private static AWTEventAccessor awtEventAccessor;
    private static InputEventAccessor inputEventAccessor;
    private static MouseEventAccessor mouseEventAccessor;
    private static FrameAccessor frameAccessor;
    private static KeyboardFocusManagerAccessor kfmAccessor;
    private static MenuComponentAccessor menuComponentAccessor;
    private static EventQueueAccessor eventQueueAccessor;
    private static PopupMenuAccessor popupMenuAccessor;
    private static FileDialogAccessor fileDialogAccessor;
    private static ScrollPaneAdjustableAccessor scrollPaneAdjustableAccessor;
    private static CheckboxMenuItemAccessor checkboxMenuItemAccessor;
    private static CursorAccessor cursorAccessor;
    private static MenuBarAccessor menuBarAccessor;
    private static MenuItemAccessor menuItemAccessor;
    private static MenuAccessor menuAccessor;
    private static KeyEventAccessor keyEventAccessor;
    private static ClientPropertyKeyAccessor clientPropertyKeyAccessor;
    private static SystemTrayAccessor systemTrayAccessor;
    private static TrayIconAccessor trayIconAccessor;
    private static DefaultKeyboardFocusManagerAccessor defaultKeyboardFocusManagerAccessor;
    private static SequencedEventAccessor sequencedEventAccessor;
    private static ToolkitAccessor toolkitAccessor;
    private static InvocationEventAccessor invocationEventAccessor;
    private static SystemColorAccessor systemColorAccessor;
    private static AccessibleContextAccessor accessibleContextAccessor;
    private static AccessibleBundleAccessor accessibleBundleAccessor;
    private static DragSourceContextAccessor dragSourceContextAccessor;
    private static DropTargetContextAccessor dropTargetContextAccessor;

    /*
     * Set an accessor object for the java.awt.color.ICC_Profile class.
     */
    public static void setICC_ProfileAccessor(ICC_ProfileAccessor ipa) {
        iccProfileAccessor = ipa;
    }

    /*
     * Retrieve the accessor object for the java.awt.color.ICC_Profile class.
     */
    public static ICC_ProfileAccessor getICC_ProfileAccessor() {
        var access = iccProfileAccessor;
        if (access == null) {
            ensureClassInitialized(ICC_Profile.class);
            access = iccProfileAccessor;
        }
        return access;
    }

    /*
     * Set an accessor object for the java.awt.Component class.
     */
    public static void setComponentAccessor(ComponentAccessor ca) {
        componentAccessor = ca;
    }

    /*
     * Retrieve the accessor object for the java.awt.Component class.
     */
    public static ComponentAccessor getComponentAccessor() {
        var access = componentAccessor;
        if (access == null) {
            ensureClassInitialized(Component.class);
            access = componentAccessor;
        }
        return access;
    }

    /*
     * Set an accessor object for the java.awt.Container class.
     */
    public static void setContainerAccessor(ContainerAccessor ca) {
        containerAccessor = ca;
    }

    /*
     * Retrieve the accessor object for the java.awt.Container class.
     */
    public static ContainerAccessor getContainerAccessor() {
        var access = containerAccessor;
        if (access == null) {
            ensureClassInitialized(Container.class);
            access = containerAccessor;
        }
        return access;
    }

    /*
     * Set an accessor object for the java.awt.Window class.
     */
    public static void setWindowAccessor(WindowAccessor wa) {
        windowAccessor = wa;
    }

    /*
     * Retrieve the accessor object for the java.awt.Window class.
     */
    public static WindowAccessor getWindowAccessor() {
        var access = windowAccessor;
        if (access == null) {
            ensureClassInitialized(Window.class);
            access = windowAccessor;
        }
        return access;
    }

    /*
     * Set an accessor object for the java.awt.AWTEvent class.
     */
    public static void setAWTEventAccessor(AWTEventAccessor aea) {
        awtEventAccessor = aea;
    }

    /*
     * Retrieve the accessor object for the java.awt.AWTEvent class.
     */
    public static AWTEventAccessor getAWTEventAccessor() {
        var access = awtEventAccessor;
        if (access == null) {
            ensureClassInitialized(AWTEvent.class);
            access = awtEventAccessor;
        }
        return access;
    }

    /*
     * Set an accessor object for the java.awt.event.InputEvent class.
     */
    public static void setInputEventAccessor(InputEventAccessor iea) {
        inputEventAccessor = iea;
    }

    /*
     * Retrieve the accessor object for the java.awt.event.InputEvent class.
     */
    public static InputEventAccessor getInputEventAccessor() {
        var access = inputEventAccessor;
        if (access == null) {
            ensureClassInitialized(InputEvent.class);
            access = inputEventAccessor;
        }
        return access;
    }

    /*
     * Set an accessor object for the java.awt.event.MouseEvent class.
     */
    public static void setMouseEventAccessor(MouseEventAccessor mea) {
        mouseEventAccessor = mea;
    }

    /*
     * Retrieve the accessor object for the java.awt.event.MouseEvent class.
     */
    public static MouseEventAccessor getMouseEventAccessor() {
        var access = mouseEventAccessor;
        if (access == null) {
            ensureClassInitialized(MouseEvent.class);
            access = mouseEventAccessor;
        }
        return access;
    }

    /*
     * Set an accessor object for the java.awt.Frame class.
     */
    public static void setFrameAccessor(FrameAccessor fa) {
        frameAccessor = fa;
    }

    /*
     * Retrieve the accessor object for the java.awt.Frame class.
     */
    public static FrameAccessor getFrameAccessor() {
        var access = frameAccessor;
        if (access == null) {
            ensureClassInitialized(Frame.class);
            access = frameAccessor;
        }
        return access;
    }

    /*
     * Set an accessor object for the java.awt.KeyboardFocusManager class.
     */
    public static void setKeyboardFocusManagerAccessor(KeyboardFocusManagerAccessor kfma) {
        kfmAccessor = kfma;
    }

    /*
     * Retrieve the accessor object for the java.awt.KeyboardFocusManager class.
     */
    public static KeyboardFocusManagerAccessor getKeyboardFocusManagerAccessor() {
        var access = kfmAccessor;
        if (access == null) {
            ensureClassInitialized(KeyboardFocusManager.class);
            access = kfmAccessor;
        }
        return access;
    }

    /*
     * Set an accessor object for the java.awt.MenuComponent class.
     */
    public static void setMenuComponentAccessor(MenuComponentAccessor mca) {
        menuComponentAccessor = mca;
    }

    /*
     * Retrieve the accessor object for the java.awt.MenuComponent class.
     */
    public static MenuComponentAccessor getMenuComponentAccessor() {
        var access = menuComponentAccessor;
        if (access == null) {
            ensureClassInitialized(MenuComponent.class);
            access = menuComponentAccessor;
        }
        return access;
    }

    /*
     * Set an accessor object for the java.awt.EventQueue class.
     */
    public static void setEventQueueAccessor(EventQueueAccessor eqa) {
        eventQueueAccessor = eqa;
    }

    /*
     * Retrieve the accessor object for the java.awt.EventQueue class.
     */
    public static EventQueueAccessor getEventQueueAccessor() {
        var access = eventQueueAccessor;
        if (access == null) {
            ensureClassInitialized(EventQueue.class);
            access = eventQueueAccessor;
        }
        return access;
    }

    /*
     * Set an accessor object for the java.awt.PopupMenu class.
     */
    public static void setPopupMenuAccessor(PopupMenuAccessor pma) {
        popupMenuAccessor = pma;
    }

    /*
     * Retrieve the accessor object for the java.awt.PopupMenu class.
     */
    public static PopupMenuAccessor getPopupMenuAccessor() {
        var access = popupMenuAccessor;
        if (access == null) {
            ensureClassInitialized(PopupMenu.class);
            access = popupMenuAccessor;
        }
        return access;
    }

    /*
     * Set an accessor object for the java.awt.FileDialog class.
     */
    public static void setFileDialogAccessor(FileDialogAccessor fda) {
        fileDialogAccessor = fda;
    }

    /*
     * Retrieve the accessor object for the java.awt.FileDialog class.
     */
    public static FileDialogAccessor getFileDialogAccessor() {
        var access = fileDialogAccessor;
        if (access == null) {
            ensureClassInitialized(FileDialog.class);
            access = fileDialogAccessor;
        }
        return access;
    }

    /*
     * Set an accessor object for the java.awt.ScrollPaneAdjustable class.
     */
    public static void setScrollPaneAdjustableAccessor(ScrollPaneAdjustableAccessor adj) {
        scrollPaneAdjustableAccessor = adj;
    }

    /*
     * Retrieve the accessor object for the java.awt.ScrollPaneAdjustable
     * class.
     */
    public static ScrollPaneAdjustableAccessor getScrollPaneAdjustableAccessor() {
        var access = scrollPaneAdjustableAccessor;
        if (access == null) {
            ensureClassInitialized(ScrollPaneAdjustable.class);
            access = scrollPaneAdjustableAccessor;
        }
        return access;
    }

    /**
     * Set an accessor object for the java.awt.CheckboxMenuItem class.
     */
    public static void setCheckboxMenuItemAccessor(CheckboxMenuItemAccessor cmia) {
        checkboxMenuItemAccessor = cmia;
    }

    /**
     * Retrieve the accessor object for the java.awt.CheckboxMenuItem class.
     */
    public static CheckboxMenuItemAccessor getCheckboxMenuItemAccessor() {
        var access = checkboxMenuItemAccessor;
        if (access == null) {
            ensureClassInitialized(CheckboxMenuItemAccessor.class);
            access = checkboxMenuItemAccessor;
        }
        return access;
    }

    /**
     * Set an accessor object for the java.awt.Cursor class.
     */
    public static void setCursorAccessor(CursorAccessor ca) {
        cursorAccessor = ca;
    }

    /**
     * Retrieve the accessor object for the java.awt.Cursor class.
     */
    public static CursorAccessor getCursorAccessor() {
        var access = cursorAccessor;
        if (access == null) {
            ensureClassInitialized(CursorAccessor.class);
            access = cursorAccessor;
        }
        return access;
    }

    /**
     * Set an accessor object for the java.awt.MenuBar class.
     */
    public static void setMenuBarAccessor(MenuBarAccessor mba) {
        menuBarAccessor = mba;
    }

    /**
     * Retrieve the accessor object for the java.awt.MenuBar class.
     */
    public static MenuBarAccessor getMenuBarAccessor() {
        var access = menuBarAccessor;
        if (access == null) {
            ensureClassInitialized(MenuBarAccessor.class);
            access = menuBarAccessor;
        }
        return access;
    }

    /**
     * Set an accessor object for the java.awt.MenuItem class.
     */
    public static void setMenuItemAccessor(MenuItemAccessor mia) {
        menuItemAccessor = mia;
    }

    /**
     * Retrieve the accessor object for the java.awt.MenuItem class.
     */
    public static MenuItemAccessor getMenuItemAccessor() {
        var access = menuItemAccessor;
        if (access == null) {
            ensureClassInitialized(MenuItemAccessor.class);
            access = menuItemAccessor;
        }
        return access;
    }

    /**
     * Set an accessor object for the java.awt.Menu class.
     */
    public static void setMenuAccessor(MenuAccessor ma) {
        menuAccessor = ma;
    }

    /**
     * Retrieve the accessor object for the java.awt.Menu class.
     */
    public static MenuAccessor getMenuAccessor() {
        var access = menuAccessor;
        if (access == null) {
            ensureClassInitialized(MenuAccessor.class);
            access = menuAccessor;
        }
        return access;
    }

    /**
     * Set an accessor object for the java.awt.event.KeyEvent class.
     */
    public static void setKeyEventAccessor(KeyEventAccessor kea) {
        keyEventAccessor = kea;
    }

    /**
     * Retrieve the accessor object for the java.awt.event.KeyEvent class.
     */
    public static KeyEventAccessor getKeyEventAccessor() {
        var access = keyEventAccessor;
        if (access == null) {
            ensureClassInitialized(KeyEventAccessor.class);
            access = keyEventAccessor;
        }
        return access;
    }

    /**
     * Set an accessor object for the javax.swing.ClientPropertyKey class.
     */
    public static void setClientPropertyKeyAccessor(ClientPropertyKeyAccessor cpka) {
        clientPropertyKeyAccessor = cpka;
    }

    /**
     * Retrieve the accessor object for the javax.swing.ClientPropertyKey class.
     */
    public static ClientPropertyKeyAccessor getClientPropertyKeyAccessor() {
        var access = clientPropertyKeyAccessor;
        if (access == null) {
            ensureClassInitialized(ClientPropertyKeyAccessor.class);
            access = clientPropertyKeyAccessor;
        }
        return access;
    }

    /**
     * Set an accessor object for the java.awt.SystemTray class.
     */
    public static void setSystemTrayAccessor(SystemTrayAccessor sta) {
        systemTrayAccessor = sta;
    }

    /**
     * Retrieve the accessor object for the java.awt.SystemTray class.
     */
    public static SystemTrayAccessor getSystemTrayAccessor() {
        var access = systemTrayAccessor;
        if (access == null) {
            ensureClassInitialized(SystemTrayAccessor.class);
            access = systemTrayAccessor;
        }
        return access;
    }

    /**
     * Set an accessor object for the java.awt.TrayIcon class.
     */
    public static void setTrayIconAccessor(TrayIconAccessor tia) {
        trayIconAccessor = tia;
    }

    /**
     * Retrieve the accessor object for the java.awt.TrayIcon class.
     */
    public static TrayIconAccessor getTrayIconAccessor() {
        var access = trayIconAccessor;
        if (access == null) {
            ensureClassInitialized(TrayIconAccessor.class);
            access = trayIconAccessor;
        }
        return access;
    }

    /**
     * Set an accessor object for the java.awt.DefaultKeyboardFocusManager class.
     */
    public static void setDefaultKeyboardFocusManagerAccessor(DefaultKeyboardFocusManagerAccessor dkfma) {
        defaultKeyboardFocusManagerAccessor = dkfma;
    }

    /**
     * Retrieve the accessor object for the java.awt.DefaultKeyboardFocusManager class.
     */
    public static DefaultKeyboardFocusManagerAccessor getDefaultKeyboardFocusManagerAccessor() {
        var access = defaultKeyboardFocusManagerAccessor;
        if (access == null) {
            ensureClassInitialized(DefaultKeyboardFocusManagerAccessor.class);
            access = defaultKeyboardFocusManagerAccessor;
        }
        return access;
    }
    /*
     * Set an accessor object for the java.awt.SequencedEvent class.
     */
    public static void setSequencedEventAccessor(SequencedEventAccessor sea) {
        sequencedEventAccessor = sea;
    }

    /*
     * Get the accessor object for the java.awt.SequencedEvent class.
     */
    public static SequencedEventAccessor getSequencedEventAccessor() {
        var access = sequencedEventAccessor;
        if (access == null) {
            try {
                ensureClassInitialized(
                        Class.forName("java.awt.SequencedEvent"));
            } catch (ClassNotFoundException ignore) {
            }
            access = sequencedEventAccessor;
        }
        return access;
    }

    /*
     * Set an accessor object for the java.awt.Toolkit class.
     */
    public static void setToolkitAccessor(ToolkitAccessor ta) {
        toolkitAccessor = ta;
    }

    /*
     * Get the accessor object for the java.awt.Toolkit class.
     */
    public static ToolkitAccessor getToolkitAccessor() {
        var access = toolkitAccessor;
        if (access == null) {
            ensureClassInitialized(Toolkit.class);
            access = toolkitAccessor;
        }
        return access;
    }

    /*
     * Get the accessor object for the java.awt.event.InvocationEvent class.
     */
    public static void setInvocationEventAccessor(InvocationEventAccessor invocationEventAccessor) {
        AWTAccessor.invocationEventAccessor = invocationEventAccessor;
    }

    /*
     * Set the accessor object for the java.awt.event.InvocationEvent class.
     */
    public static InvocationEventAccessor getInvocationEventAccessor() {
        return invocationEventAccessor;
    }

    /*
     * Get the accessor object for the java.awt.SystemColor class.
     */
    public static SystemColorAccessor getSystemColorAccessor() {
        var access = systemColorAccessor;
        if (access == null) {
            ensureClassInitialized(SystemColor.class);
            access = systemColorAccessor;
        }
        return access;
    }

     /*
     * Set the accessor object for the java.awt.SystemColor class.
     */
     public static void setSystemColorAccessor(SystemColorAccessor systemColorAccessor) {
         AWTAccessor.systemColorAccessor = systemColorAccessor;
     }

    /*
     * Get the accessor object for the javax.accessibility.AccessibleContext class.
     */
    public static AccessibleContextAccessor getAccessibleContextAccessor() {
        var access = accessibleContextAccessor;
        if (access == null) {
            ensureClassInitialized(AccessibleContext.class);
            access = accessibleContextAccessor;
        }
        return access;
    }

   /*
    * Set the accessor object for the javax.accessibility.AccessibleBundle class.
    */
    public static void setAccessibleBundleAccessor(AccessibleBundleAccessor accessor) {
        AWTAccessor.accessibleBundleAccessor = accessor;
    }

    /*
     * Get the accessor object for the javax.accessibility.AccessibleBundle class.
     */
    public static AccessibleBundleAccessor getAccessibleBundleAccessor() {
        var access = accessibleBundleAccessor;
        if (access == null) {
            ensureClassInitialized(AccessibleBundle.class);
            access = accessibleBundleAccessor;
        }
        return access;
    }

   /*
    * Set the accessor object for the javax.accessibility.AccessibleContext class.
    */
    public static void setAccessibleContextAccessor(AccessibleContextAccessor accessor) {
        AWTAccessor.accessibleContextAccessor = accessor;
    }

    /*
     * Get the accessor object for the java.awt.dnd.DragSourceContext class.
     */
    public static DragSourceContextAccessor getDragSourceContextAccessor() {
        var access = dragSourceContextAccessor;
        if (access == null) {
            ensureClassInitialized(DragSourceContext.class);
            access = dragSourceContextAccessor;
        }
        return access;
    }

    /*
     * Set the accessor object for the java.awt.dnd.DragSourceContext class.
     */
    public static void setDragSourceContextAccessor(DragSourceContextAccessor accessor) {
        AWTAccessor.dragSourceContextAccessor = accessor;
    }

    /*
     * Get the accessor object for the java.awt.dnd.DropTargetContext class.
     */
    public static DropTargetContextAccessor getDropTargetContextAccessor() {
        var access = dropTargetContextAccessor;
        if (access == null) {
            ensureClassInitialized(DropTargetContext.class);
            access = dropTargetContextAccessor;
        }
        return access;
    }

    /*
     * Set the accessor object for the java.awt.dnd.DropTargetContext class.
     */
    public static void setDropTargetContextAccessor(DropTargetContextAccessor accessor) {
        AWTAccessor.dropTargetContextAccessor = accessor;
    }

    private static void ensureClassInitialized(Class<?> c) {
        try {
            MethodHandles.lookup().ensureInitialized(c);
        } catch (IllegalAccessException e) {}
    }
}
