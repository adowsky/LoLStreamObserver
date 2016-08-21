import React from 'react';
import ReactCSSTransitionGroup from "react-addons-css-transition-group";

export default class ButtonGroup extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {

        return <ReactCSSTransitionGroup transitionName="button-transition" className="button-group"
                                         transitionEnterTimeout={500}
                                        transitionLeaveTimeout={300}>
            {this.props.buttons.map((button) =>
                <button key={button.name} className={button.aClass} onClick={button.action}/>
            )}
        </ReactCSSTransitionGroup>
    }
}

ButtonGroup.defaultProps = {
    buttons: []
};