import React from 'react';

export default class ButtonGroup extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div className="button-group">
                {this.props.buttons.map((button) =>
                    <button key={button.name} className={button.aClass} onClick={button.action}/>
                )}
            </div>
        );
    }
}

ButtonGroup.defaultProps = {
    buttons: []
};